package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostServiceValidator postServiceValidator;
    private final PostMapper postMapper;

    @Override
    public PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto) {
        postServiceValidator.validatePostDto(postCreateRequestDto);
        Post post = postMapper.toPostEntity(postCreateRequestDto);
        Post draftPost = postRepository.save(post);
        return postMapper.toPostResponseDto(draftPost);
    }

    @Override
    public PostResponseDto publishPostDraft(Long postId) {
        Post postToPublish = getPostById(postId);
        postServiceValidator.validatePostBeforePublish(postToPublish);
        postToPublish.setPublished(true);
        postToPublish.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(postToPublish);
        return postMapper.toPostResponseDto(publishedPost);
    }

    @Override
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post postToUpdate = getPostById(postId);
        Post requestPost = postMapper.toPostEntity(postUpdateRequestDto);
        postServiceValidator.validatePostBeforeUpdate(postToUpdate, requestPost);
        Post updatedPost = postRepository.save(copyPostData(requestPost, postToUpdate));
        return postMapper.toPostResponseDto(updatedPost);
    }

    @Override
    public void deletePost(Long postId) {
        Post postToDelete = getPostById(postId);
        postToDelete.setDeleted(true);
        postRepository.save(postToDelete);
    }

    @Override
    public PostResponseDto getPost(Long postId) {
        Post post = getPostById(postId);
        return postMapper.toPostResponseDto(post);
    }

    @Override
    public List<PostResponseDto> getProjectPostDrafts(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> (Objects.equals(post.getProjectId(), projectId)
                        && !post.isPublished()
                        && !post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    @Override
    public List<PostResponseDto> getUserPostDrafts(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> (Objects.equals(post.getAuthorId(), userId)
                        && !post.isPublished()
                        && !post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    @Override
    public List<PostResponseDto> getProjectPosts(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> (Objects.equals(post.getProjectId(), projectId)
                        && post.isPublished()
                        && !post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    @Override
    public List<PostResponseDto> getUserPosts(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> (Objects.equals(post.getAuthorId(), userId)
                        && post.isPublished()
                        && !post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    private Post getPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElseThrow();
    }

    private Post copyPostData(Post sourcePost, Post targetPost) {
        targetPost.setContent(sourcePost.getContent());
        return targetPost;
    }
}
