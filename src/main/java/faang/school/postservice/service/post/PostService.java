package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.student.DtoBanShema;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.producer.PostEventProducer;
import faang.school.postservice.publisher.MessageSenderForUserBanImpl;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.entity.Resource;
import faang.school.postservice.repository.entity.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.amazons3.Amazons3ServiceImpl;
import faang.school.postservice.service.amazons3.processing.KeyKeeper;
import faang.school.postservice.sheduler.postcorrector.ginger.GingerCorrector;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import faang.school.postservice.validator.dto.project.ProjectDtoValidator;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import faang.school.postservice.validator.file.FileValidator;
import faang.school.postservice.validator.post.PostIdValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@Setter
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class PostService {

  private final PostMapper postMapper;
  private final PostRepository postRepository;
  private final UserServiceClient userService;
  private final ProjectServiceClient projectService;
  private final AlbumService albumService;
  private final ResourceServiceImpl resourceServiceImpl;
  private final UserDtoValidator userDtoValidator;
  private final ProjectDtoValidator projectDtoValidator;
  private final PostIdValidator postIdValidator;
  private final Amazons3ServiceImpl amazonS3;
  private final FileValidator fileValidator;
  private final KeyKeeper keyKeeper;
  private final GingerCorrector gingerCorrector;
  private final MessageSenderForUserBanImpl messageSenderForUserBan;
  private final ObjectMapper objectMapper;
  private final PostEventProducer postEventProducer;
  private final PostCacheRepository postCacheRepository;
  private final UserCacheRepository userCacheRepository;
  private final UserMapper userMapper;

  @Value("${size.not-verified-posts-for-users}")
  private int sizeNotVerifiedPostsForUsers;

  @Transactional
  public PostDraftResponseDto createDraftPost(@NotNull @Valid PostDraftCreateDto dto) {
    validateUserOrProject(dto.getAuthorId(), dto.getProjectId());

    Post postEntity = postMapper.toEntityFromDraftDto(dto);
    if (dto.getAlbumsId() != null) {
      postEntity.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
    }
    if (dto.getResourcesId() != null) {
      postEntity.setResources(resourceServiceImpl.getResourcesByIds(dto.getResourcesId()));
    }
    return postMapper.toDraftDtoFromPost(postRepository.save(postEntity));
  }

  @Transactional
  public PostDraftResponseDto createDraftPostWithFiles(
      PostDraftWithFilesCreateDto dto, MultipartFile[] files) throws IOException {
    validateUserOrProject(dto.getAuthorId(), dto.getProjectId());
    fileValidator.checkFiles(files);
    Post post = postMapper.toEntityFromDraftDtoWithFiles(dto);
    if (dto.getAlbumsId() != null) {
      post.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
    }

    List<Resource> resources = new ArrayList<>();
    uploadAndAddFiles(resources, files, post);
    post.setResources(resources);
    return postMapper.toDraftDtoFromPost(postRepository.save(post));
  }

  public PostResponseDto publishPost(@Positive long postId) {
    Post post = getPostById(postId);
    if (post.isPublished()) {
      throw new IllegalArgumentException("Post is already published");
    }
    post.setPublished(true);
    post.setPublishedAt(LocalDateTime.now());

    Post publishedPost = postRepository.save(post);

    UserDto userDto = userService.getUser(post.getAuthorId());

    saveAuthorToCache(userDto);
    savePostToCache(publishedPost, userDto);
    sendPostToMessageBroker(publishedPost);

    return postMapper.toDtoFromPost(publishedPost);
  }

  private void saveAuthorToCache(UserDto userDto) {
    UserCache userCache = userMapper.toUserCache(userDto);
    userCacheRepository.save(userCache);
  }

  private void savePostToCache(Post post, UserDto userDto) {
    PostCache postCache = postMapper.toPostCache(post);
    postCache.setAuthorName(userDto.getUsername());
    postCacheRepository.save(postCache);
  }

  private void sendPostToMessageBroker(Post publishedPost) {
    List<Long> followers = userService.getUserSubscribers(publishedPost.getAuthorId()).stream()
        .map(UserDto::getId).toList();

    PostEventDto postEvent = PostEventDto.builder()
        .posId(publishedPost.getId())
        .followers(followers)
        .build();

    postEventProducer.sendEvent(postEvent);
  }

  public PostResponseDto updatePost(@Positive long postId, @NotNull @Valid PostUpdateDto dto) {
    Post post = getPostById(postId);
    post.setContent(dto.getContent());
    return postMapper.toDtoFromPost(postRepository.save(post));
  }

  @Transactional
  public PostResponseDto updatePostWithFiles(
      @Positive long postId, @NotNull @Valid PostUpdateDto dto, MultipartFile[] files)
      throws IOException {
    fileValidator.checkFiles(files);
    Post post = getPostById(postId);
    fileValidator.checkingTotalOfFiles(files.length, post.getResources().size());

    List<Resource> resourcesFromPostInDateBase = post.getResources();
    List<Resource> newResourcesToUpdate = resourceServiceImpl.getResourcesByIds(
        dto.getResourcesIds());

    removeIrrelevantResourcesFromMinio(resourcesFromPostInDateBase, newResourcesToUpdate);
    uploadAndAddFiles(newResourcesToUpdate, files, post);

    post.setResources(newResourcesToUpdate);
    post.setContent(dto.getContent());
    return postMapper.toDtoFromPost(postRepository.save(post));
  }

  public PostResponseDto deletePost(@Positive long postId) {
    Post post = getPostById(postId);
    post.setDeleted(true);
    return postMapper.toDtoFromPost(postRepository.save(post));
  }

  public Post findPostById(Long postId) {
    postIdValidator.postIdValidate(postId);
    return postRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
  }

  public PostResponseDto getPost(@Positive long postId) {
    return postMapper.toDtoFromPost(getPostById(postId));
  }

  public boolean existsPost(Long postId) {
    postIdValidator.postIdValidate(postId);
    return postRepository.existsById(postId);
  }

  public List<PostDraftResponseDto> getDraftPostsByUserIdSortedCreatedAtDesc(long userId) {
    return postRepository.findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId)
        .stream()
        .map(postMapper::toDraftDtoFromPost)
        .toList();
  }

  public List<PostDraftResponseDto> getDraftPostsByProjectIdSortedCreatedAtDesc(long projectId) {
    return postRepository.findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId)
        .stream()
        .map(postMapper::toDraftDtoFromPost)
        .toList();
  }

  public List<PostResponseDto> getPublishPostsByUserIdSortedCreatedAtDesc(long userId) {
    return postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
        .map(postMapper::toDtoFromPost)
        .toList();
  }

  public List<PostResponseDto> getPublishPostsByProjectIdSortedCreatedAtDesc(long projectId) {
    return postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId)
        .stream()
        .map(postMapper::toDtoFromPost)
        .toList();
  }

  @Async("workerPool")
  public void checkingPostForErrors() throws IOException, InterruptedException {
    List<Post> posts = postRepository.findByNotPublished();

    int batchSize = 5;
    int totalPosts = posts.size();
    for (int i = 0; i < totalPosts; i += batchSize) {
      int end = Math.min(i + batchSize, totalPosts);
      List<Post> batchPosts = posts.subList(i, end);

      checkingGroupPost(batchPosts);
    }
  }

  @Async("workerPool")
  public void checkPostsForVerification() throws IOException {
    List<Post> posts = postRepository.findByNotVerified();

    List<Long> userIds = getBannedUsers(posts);
    if (userIds == null || userIds.isEmpty()) {
      log.info("Users' posts are in good shape");
      return;
    }
    DtoBanShema dtoBanShema = new DtoBanShema();
    dtoBanShema.setIds(userIds);
    messageSenderForUserBan.send(objectMapper.writeValueAsString(dtoBanShema));
    log.info("users sent to block");
  }

  private void checkingGroupPost(List<Post> batchPosts) throws IOException, InterruptedException {
    List<Post> checkPosts = gingerCorrector.correct(batchPosts);
    postRepository.saveAll(checkPosts);
  }

  private List<Long> getBannedUsers(List<Post> posts) {
    System.out.println(sizeNotVerifiedPostsForUsers);
    return posts.stream()
        .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
        .entrySet().stream()
        .filter(user -> user.getValue() >= sizeNotVerifiedPostsForUsers)
        .map(Map.Entry::getKey)
        .toList();
  }

  private Post getPostById(@Positive long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));
  }

  private void validateUserOrProject(Long userId, Long projectId) {
    if (userId != null) {
      userDtoValidator.validateUserDto(userService.getUser(userId));
    }
    if (projectId != null) {
      projectDtoValidator.validateProjectDto(projectService.getProject(projectId));
    }
  }

  private void uploadAndAddFiles(
      List<Resource> resources, MultipartFile[] files, Post post) throws IOException {
    String folder = String.format("%d:%s:%d", post.getAuthorId(), "files", post.getProjectId());
    for (MultipartFile file : files) {
      String key = keyKeeper.getKeyFile(folder);
      amazonS3.uploadFile(file, key);
      Resource resource = createdResource(file, key);
      resource.setPost(post);
      resources.add(resource);
    }
  }

  private void removeIrrelevantResourcesFromMinio(List<Resource> resourcesFromDateBase,
      List<Resource> resourcesToUpdate) {
    if (resourcesToUpdate == null) {
      resourcesToUpdate = new ArrayList<>();
    }
    for (Resource resource : resourcesFromDateBase) {
      if (!resourcesToUpdate.contains(resource)) {
        amazonS3.deleteFile(resource.getKey());
      }
    }
  }

  private Resource createdResource(MultipartFile file, String key) {
    return resourceServiceImpl.save(
        Resource.builder()
            .name(file.getOriginalFilename())
            .key(key)
            .size(file.getSize())
            .type(file.getContentType())
            .build());
  }

  @Transactional
  public void publishScheduledPosts(@Positive int subListSize) {
    List<Post> posts = postRepository.findReadyToPublish();
    if (posts.isEmpty()) {
      log.info("No posts to publish");
      return;
    }
    List<List<Post>> partitionsPosts = ListUtils.partition(posts, subListSize);
    for (List<Post> partitionPosts : partitionsPosts) {
      asyncPublishPosts(partitionPosts);
    }
    log.info("Published all {} posts", posts.size());
  }

  @Async("executorPostPublisher")
  protected void asyncPublishPosts(List<Post> posts) {
    CompletableFuture.runAsync(() -> {
      posts.forEach(post -> {

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        /* задать вопрос с асинх или сущностью Post, вылетает исключение
        Unable to evaluate the expression Method threw 'org.hibernate.LazyInitializationException' exception.
        а так вообще мне не нравится этот метод, посмотреть, кто его писал и спросить
        Long userId = post.getAuthorId();
        UserDto userDto = userService.getUser(userId);
        saveAuthorToCache(userDto);
        savePostToCache(post, userDto);
        sendPostToMessageBroker(post);
        */
      });
      postRepository.saveAll(posts);
      log.info("Published {} posts", posts.size());
    });
  }
}