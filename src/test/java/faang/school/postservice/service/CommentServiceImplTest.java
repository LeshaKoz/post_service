package faang.school.postservice.service;

import faang.school.postservice.dto.comment.BanAuthorByCommentsDto;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RedisService redisService;
    @InjectMocks
    private CommentServiceImpl commentServiceImpl;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(commentServiceImpl, "maxUnverifiedComments", 3);
    }

    @Test
    public void collectAndPushUsersForBanTest() {
        when(commentRepository.getUnverifiedCommentAuthorCountDto()).thenReturn(
                List.of(new BanAuthorByCommentsDto(1L, 2L),
                        new BanAuthorByCommentsDto(3L, 4L)));

        commentServiceImpl.collectAndPushUsersForBan();

        verify(redisService, times(1)).pushToRedisUsersForBan(anyLong());
    }
}
