package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.mapper.CacheMapperImpl;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private CacheMapperImpl cacheMapperIml;
    @InjectMocks
    private CacheService cacheService;
    private PostReadDto dto;
    PostCache cache;


    @BeforeEach
    void setUp() {
        dto = PostReadDto.builder()
                .id(1L)
                .authorId(100L)
                .content("Test")
                .build();

        cache = PostCache.builder()
                .postId(1L)
                .authorId(100L)
                .content("Test")
                .build();


    }

    @Test
    @DisplayName("Успешное сохранение поста в кэш")
    void shouldSavePostToCache() {
        when(cacheMapperIml.toPostCache(dto)).thenReturn(cache);

        cacheService.savePost(dto);

        verify(cacheMapperIml).toPostCache(dto);
        verify(postCacheRepository).save(cache);
    }

}
