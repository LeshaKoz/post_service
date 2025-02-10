package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserContext userContext;
    private final PostSchedulerService postSchedulerService;
    private final ModerationDictionary moderationDictionary;
    private final PaginationService paginationService;
    private final PostProperties postProperties;
    @Value("${post.schedule.batch-size}")
    private int batchSize;

    public PostReadDto createPostDraft(PostCreateDto dto) {
        validateCreateDraftDto(dto);
        verifyHashtagsExists(dto.getHashtagIds());

        Post post = postMapper.toEntity(dto);
        if (dto.getHashtagIds() != null) {
            List<Hashtag> hashtags = dto.getHashtagIds().stream()
                    .map(hashtagService::getHashtagById)
                    .toList();
            post.setHashtags(hashtags);
        }

        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostReadDto publishPost(long id) {
        Post post = getPostById(id);
        if (post.isPublished()) {
            throw new BusinessException("Пост уже опубликован");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto updatePost(long id, PostUpdateDto dto) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост удалён");
        }
        verifyHashtagsExists(dto.getHashtagIds());

        postMapper.updateEntityFromDto(dto, post);

        if (dto.getHashtagIds() != null) {
            List<Hashtag> hashtags = dto.getHashtagIds().stream()
                    .map(hashtagService::getHashtagById)
                    .toList();
            post.setHashtags(hashtags);
        }

        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto softDeletePost(long id) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост уже удален");
        }
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostReadDto> getAllDrafts(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllDraftsByAuthorId(id),
                () -> postRepository.findAllDraftsByProjectId(id)
        );
    }

    public List<PostReadDto> getAllPublished(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllPublishedByAuthorId(id),
                () -> postRepository.findAllPublishedByProjectId(id)
        );
    }

    public Post getPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден"));
    }

    public List<PostReadDto> getPostsByHashtagId(long hashtagId) {
        List<Post> posts = postRepository.findAllByHashtagId(hashtagId);
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(Post::isPublished)
                .map(postMapper::toDto)
                .toList();
    }

    public void moderatePosts() {
        concurrencyProcessPosts(
                postRepository::findAllNotVerified,
                this::moderatePostsBatch,
                postProperties.getPageSize(),
                postProperties.getBatchSize()
        );
    }

    private Stream<Post> moderatePostsBatch(List<Post> posts) {
        LocalDateTime verifiedDate = LocalDateTime.now();
        return posts.parallelStream()
                .filter(post -> moderationDictionary.isAllowed(post.getContent()))
                .peek(post -> {
                    post.setVerified(true);
                    post.setVerifiedDate(verifiedDate);
                });
    }

    private void concurrencyProcessPosts(
            Function<Pageable, Page<Post>> getPostsFunction,
            Function<List<Post>, Stream<Post>> processFunction,
            int pageSize,
            int batchSize
    ) {
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Post> page;
        int pageNumber = 0;

        do {
            page = getPostsFunction.apply(pageable);
            List<Post> postsToSave = paginationService.processInParallel(
                    page.getContent(),
                    batchSize,
                    processFunction
            );
            postRepository.saveAll(postsToSave);
            pageNumber++;
            pageable = PageRequest.of(pageNumber, pageSize);
        } while (!page.isLast());
    }

    private List<PostReadDto> getAllPostByCondition(
            PostOwnerType ownerType,
            Supplier<List<Post>> authorSupplier,
            Supplier<List<Post>> projetcSupplier
    ) {
        List<Post> postStream = switch (ownerType) {
            case AUTHOR:
                yield authorSupplier.get();
            case PROJECT:
                yield projetcSupplier.get();
        };
        return postStream.stream()
                .map(postMapper::toDto)
                .toList();
    }

    private void validateCreateDraftDto(PostCreateDto dto) {
        var authorId = dto.getAuthorId();
        var projectId = dto.getProjectId();

        if (authorId != null && projectId != null) {
            throw new BusinessException("Пост может создать либо автор, либо проект.");
        }

        if (authorId != null) {
            userContext.setUserId(authorId);
            if (userServiceClient.getUser(authorId) == null) {
                throw new EntityNotFoundException("Пользователь не найден");
            }
        } else if (projectId != null) {
            userContext.setUserId(projectId);
            if (projectServiceClient.getProject(projectId) == null) {
                throw new EntityNotFoundException("Проект не найден");
            }
        }
    }

    private void verifyHashtagsExists(List<Long> hashtagIds) {
        if (hashtagIds == null) {
            return;
        }
        List<Long> missingHashtagIds = hashtagIds.stream()
                .filter(hashtagId -> !hashtagService.isHashtagExist(hashtagId))
                .toList();

        if (!missingHashtagIds.isEmpty()) {
            throw new EntityNotFoundException("Хэштеги со ID: " + missingHashtagIds + " не найдены");
        }
    }

    public void publishScheduledPosts() {
        postSchedulerService.publishScheduledPosts(batchSize);
    }
}