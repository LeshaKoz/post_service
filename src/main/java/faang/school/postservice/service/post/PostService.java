package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto dto);

    PostResponseDto publishPost(long postId);

    List<PostResponseDto> getPostsByHashtag(String hashtag);

    void banUsersWithManyUnverifiedPosts();

    PostResponseDto getPostById(long postId);

    List<PostResponseDto> getLatestPosts(List<String> followers, int limit);
}
