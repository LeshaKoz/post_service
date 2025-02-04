package faang.school.postservice.service;

import faang.school.postservice.dto.Post.CreatePostDraftDto;
import faang.school.postservice.dto.Post.PostResponseDto;
import faang.school.postservice.dto.Post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    public PostResponseDto createDraft(CreatePostDraftDto postDraftDto) {
        Post post = postMapper.fromCreateDto(postDraftDto);
        postValidator.validatePostAuthorExist(post);
        postValidator.validatePostDraftInfo(post);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto publishPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        postValidator.validateNotPublished(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto updatePost(UpdatePostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postDto.getId()));
        post = postMapper.update(post, postDto);
        postValidator.validatePostDraftInfo(post);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto safeDeletePost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto getPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        return postMapper.toResponseDto(post);
    }

    public List<PostResponseDto> getUserDrafts(long userId) {
        return getExistingPostsSortedByDate(postRepository::findByAuthorId, Post::getCreatedAt, userId, false);
    }

    public List<PostResponseDto> getProjectDrafts(long projectId) {
        return getExistingPostsSortedByDate(postRepository::findByProjectId, Post::getCreatedAt, projectId, false);
    }

    public List<PostResponseDto> getUserPosts(long userId) {
        return getExistingPostsSortedByDate(postRepository::findByAuthorId, Post::getPublishedAt, userId, true);
    }

    public List<PostResponseDto> getProjectPosts(long projectId) {
        return getExistingPostsSortedByDate(postRepository::findByProjectId, Post::getPublishedAt, projectId, true);
    }

    private List<PostResponseDto> getExistingPostsSortedByDate(
            Function<Long, List<Post>> repositoryMethod,
            Function<Post, LocalDateTime> fieldToSortBy,
            Long id, boolean published) {
        return repositoryMethod.apply(id).stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(fieldToSortBy).reversed())
                .map(postMapper::toResponseDto)
                .toList();
    }
}
