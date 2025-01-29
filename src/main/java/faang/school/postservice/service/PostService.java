package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;

import java.util.List;

public interface PostService {
    PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto);

    PostResponseDto publishPostDraft(Long postId);

    PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto);

    void deletePost(Long postId);

    PostResponseDto getPost(Long Id);

    List<PostResponseDto> getProjectPostDrafts(Long projectId);

    List<PostResponseDto> getUserPostDrafts(Long userId);

    List<PostResponseDto> getProjectPosts(Long projectId);

    List<PostResponseDto> getUserPosts(Long userId);


}
