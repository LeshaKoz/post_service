package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;

import java.util.List;

public interface PostService {
    PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto);

    PostResponseDto publishPostDraft(Long postId);

    void publishScheduledPosts();

    PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto);

    void deletePost(Long postId);

    PostResponseDto getPostWithCache(Long Id);
    void incrementPostLikesCounter(long postId);
    void decrementPostLikesCounter(long postId);

    List<PostResponseDto> findAllByFilter(PostFilterDto filter);
}
