package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public PostResponseDto createPostDraft(PostRequestDto postRequestDto) {
        postServiceValidator.validatePostDto(postRequestDto);
        Post post = new Post();
        postRepository.save(post);
        return null;
    }

    @Override
    public PostResponseDto publishPostDraft(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        postServiceValidator.validatePostExists(postId, optionalPost);
        Post postToPublish = optionalPost.orElse(new Post());
        postServiceValidator.validatePostBeforePublish(postToPublish);
        postToPublish.setPublished(true);
        postToPublish.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(postToPublish);

        return postMapper.toPostResponseDto(publishedPost);
    }

    @Override
    public PostResponseDto updatePost(PostRequestDto postRequestDto) {
        postServiceValidator.validatePostDto(postRequestDto);
        Post post = new Post();
        postRepository.save(post);
        return null;
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostResponseDto getPost(Long Id) {
        postRepository.findById(Id);
        return null;
    }

    @Override
    public List<PostResponseDto> getProjectPostDrafts(Long projectId) {
        postRepository.findByProjectId(projectId);
        return null;
    }

    @Override
    public List<PostResponseDto> getUserPostDrafts(Long userId) {
        postRepository.findByAuthorId(userId);
        return null;
    }

    @Override
    public List<PostResponseDto> getProjectPosts(Long projectId) {
        postRepository.findByProjectId(projectId);
        return null;
    }

    @Override
    public List<PostResponseDto> getUserPosts(Long userId) {
        postRepository.findByAuthorId(userId);
        return null;
    }
}
