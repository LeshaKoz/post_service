package faang.school.postservice.service.cache;

import faang.school.postservice.dto.posts.PostRedis;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.utils.PostRedisMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisCacheTest {
    @Mock
    private PostRedisRepository postRedisRepository;
    @Mock
    private PostRedisMapper mapper;
    @InjectMocks
    private RedisCache redisCache;

    @Test
    public void cachePostTest_Success() {
        long postId = 5L;
        String content = "Some content";
        Post post = Post.builder()
                .id(postId)
                .content(content)
                .build();

        when(mapper.toDto(post)).thenReturn(
                PostRedis.builder()
                        .id(postId)
                        .content(content)
                        .build());

        redisCache.cachePost(post);

        ArgumentCaptor<PostRedis> captor = ArgumentCaptor.forClass(PostRedis.class);
        verify(postRedisRepository).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getId(), postId);
        Assertions.assertEquals(captor.getValue().getContent(), content);
    }
}
