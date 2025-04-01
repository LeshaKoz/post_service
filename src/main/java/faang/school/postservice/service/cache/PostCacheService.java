package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.PostResponseDto;

import java.util.List;
import java.util.Optional;

public interface PostCacheService {

    void cachePost(PostResponseDto post);

    Optional<PostResponseDto> getCachedPost(long postId);

    List<PostResponseDto> getCachedPosts(List<String> authorIds, int limit);

    void cachePosts(List<PostResponseDto> posts);

}