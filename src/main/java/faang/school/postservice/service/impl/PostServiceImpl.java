package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.filter.post.PostSpecificationFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${post_service.batch_size}")
    private int postBatchSize;

    private final PostRepository postRepository;
    private final PostServiceValidator postServiceValidator;
    private final PostMapper postMapper;
    private final List<PostSpecificationFilter> postSpecificationFilters;
    private final ExecutorService executorService;
    private final KafkaPostProducer kafkaPostProducer;

    @Override
    public PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto) {
        postServiceValidator.validatePostDto(postCreateRequestDto);
        Post post = postMapper.toPostEntity(postCreateRequestDto);
        Post draftPost = postRepository.save(post);
        kafkaPostProducer.sendPostCreatedEvent(draftPost.getId(), draftPost.getAuthorId());
        log.info("Post draft created, id = {}", draftPost.getId());
        return postMapper.toPostResponseDto(draftPost);
    }

    @Override
    public PostResponseDto publishPostDraft(Long postId) {
        Post postToPublish = getPostById(postId);
        postServiceValidator.validatePostBeforePublish(postToPublish);
        postToPublish.setPublished(true);
        postToPublish.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(postToPublish);
        log.info("Draft post is published, id = {}", publishedPost.getId());

        return postMapper.toPostResponseDto(publishedPost);
    }

    @Override
    public void publishScheduledPosts() {
        PostFilterDto postFilterDto = PostFilterDto.builder()
                .isPublished(false)
                .isDeleted(false)
                .shouldBePublishedBefore(LocalDateTime.now())
                .build();

        List<Post> scheduledPosts = findAllPostsByFilter(postFilterDto);
        List<List<Post>> postBatches = ListUtils.partition(scheduledPosts, postBatchSize);
        postBatches.stream()
                .map(this::preparePostList)
                .map(postsBatch -> CompletableFuture.runAsync(() -> {
                    postRepository.saveAll(postsBatch);
                }, executorService).exceptionally(error -> {
                    log.error("Error processing scheduled posts", error);
                    throw new RuntimeException("Failed to process scheduled posts", error);
                })).forEach(CompletableFuture::join);
    }

    @Override
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post postToUpdate = getPostById(postId);
        Post requestPost = postMapper.toPostEntity(postUpdateRequestDto);
        Post updatedPost = postRepository.save(copyPostData(requestPost, postToUpdate));
        log.info("Post is updated, id = {}", updatedPost.getId());
        return postMapper.toPostResponseDto(updatedPost);
    }

    @Override
    public void deletePost(Long postId) {
        Post postToDelete = getPostById(postId);
        postToDelete.setDeleted(true);
        log.info("Post is deleted, id = {}", postToDelete.getId());
        postRepository.save(postToDelete);
    }

    @Override
    public PostResponseDto getPost(Long postId) {
        Post post = getPostById(postId);
        return postMapper.toPostResponseDto(post);
    }

    @Override
    public List<PostResponseDto> findAllByFilter(PostFilterDto filter) {
        return postMapper.toPostResponseDtos(findAllPostsByFilter(filter));
    }

    private List<Post> preparePostList(List<Post> posts) {
        return posts.stream()
                .map(this::preparePostToPublish)
                .toList();
    }

    private Post preparePostToPublish(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return post;
    }

    private List<Post> findAllPostsByFilter(PostFilterDto filter) {
        Specification<Post> specification = getPostSpecification(filter);
        return postRepository.findAll(specification);
    }

    private Post getPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElseThrow(() -> new IllegalArgumentException("Post not found, Id = " + postId));
    }

    private Post copyPostData(Post sourcePost, Post targetPost) {
        targetPost.setContent(sourcePost.getContent());
        return targetPost;
    }

    private Specification<Post> getPostSpecification(PostFilterDto filter) {
        return postSpecificationFilters.stream()
                .filter(spec -> spec.isApplicable(filter))
                .map(spec -> spec.apply(filter))
                .reduce(Specification::and)
                .orElse(null);
    }
}
