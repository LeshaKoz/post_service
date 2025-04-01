package faang.school.postservice.service.cache;

import faang.school.postservice.dto.user.UserDto;

import java.util.Optional;

public interface AuthorCacheService {

    void cacheAuthor(long postId, UserDto user);

    Optional<UserDto> getCachedAuthor(Long authorId);
}
