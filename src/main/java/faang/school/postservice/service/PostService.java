package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDtoRs;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto);

    PostResponseDto publishPostDraft(Long postId);

    void publishScheduledPosts();

    PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto);

    void deletePost(Long postId);

    PostResponseDto getPost(Long Id);

    List<PostResponseDto> findAllByFilter(PostFilterDto filter);

    List<ResourceDtoRs> uploadFiles(long postId, MultipartFile[] files);

}
