package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.mapper.CacheMapper;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final PostCacheRepository postCacheRepository;
    private final CacheMapper cacheMapper;

    @Async
    public void savePost(PostReadDto postReadDto) {
        var postCache = cacheMapper.toPostCache(postReadDto);
        postCacheRepository.save(postCache);
    }
}
