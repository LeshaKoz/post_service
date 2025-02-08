package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.filter.post.PostSpecificationFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostServiceValidator postServiceValidator;
    private final PostMapper postMapper;
    private final List<PostSpecificationFilter> postSpecificationFilters;

    @Override
    public PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto) {
        postServiceValidator.validatePostDto(postCreateRequestDto);
        Post post = postMapper.toPostEntity(postCreateRequestDto);
        Post draftPost = postRepository.save(post);
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
        Specification<Post> specification = getPostSpecification(filter);
        return postMapper.toPostResponseDtos(postRepository.findAll(specification));
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
