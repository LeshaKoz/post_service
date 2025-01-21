package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;

import java.util.List;

public interface PostService {
    PostResponseDto createPostDraft(PostRequestDto postRequestDto);

    PostResponseDto publishPostDraft(Long postId);

    PostResponseDto updatePost(PostRequestDto postRequestDto);

    void deletePost(Long postId);

    PostResponseDto getPost(Long Id);

    List<PostResponseDto> getProjectPostDrafts(Long projectId);

    List<PostResponseDto> getUserPostDrafts(Long userId);

    List<PostResponseDto> getProjectPosts(Long projectId);

    List<PostResponseDto> getUserPosts(Long userId);


}
