package faang.school.postservice.service;

import faang.school.postservice.exception.DataAccessException;
import faang.school.postservice.publisher.RedisUserBanTopicPublisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    public static final int POST_COUNT_THRESHOLD = 3;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisUserBanTopicPublisher redisUserBanTopicPublisher;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(postService, "postCountThreshold", POST_COUNT_THRESHOLD);
    }

    @Test
    public void shouldBanUsersIfRequired_callPublish_whenThereAreSomeUsersToBan() {
        List<Long> userIdsToBan = List.of(1L, 2L, 3L);
        when(postRepository.findAuthorIdsByUnverifiedPostsThreshold(3)).thenReturn(userIdsToBan);
        postService.banUsersIfRequired();

        for (var userId : userIdsToBan) {
            verify(redisUserBanTopicPublisher).publish(userId);
        }
        verify(redisUserBanTopicPublisher, times(userIdsToBan.size())).publish(any());
    }

    @Test
    public void shouldBanUsersIfRequired_doNotCallPublish_whenThereAreNotUsersToBan() {
        when(postRepository.findAuthorIdsByUnverifiedPostsThreshold(3)).thenReturn(List.of());
        postService.banUsersIfRequired();

        verify(redisUserBanTopicPublisher, never()).publish(any());
    }

    @Test
    public void shouldBanUsersIfRequired_throwDataAccessException_whenRepositoryThrowsDataAccessException() {
        when(postRepository.findAuthorIdsByUnverifiedPostsThreshold(POST_COUNT_THRESHOLD))
                .thenThrow(new DataAccessException("Database error"));

        assertThrows(faang.school.postservice.exception.DataAccessException.class,
                () -> postService.banUsersIfRequired());
    }
}