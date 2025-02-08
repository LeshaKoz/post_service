package faang.school.postservice.handler;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostRedisMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentEventHandlerTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRedisRepository postRedisRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PostRedisMapper postRedisMapper;
    @Mock
    private ExpirableLockRegistry redisLockRegistry;
    @Mock
    private Lock lock;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaCommentEventHandler kafkaCommentEventHandler;

    private CommentEvent commentEvent;
    private Comment comment;
    private CommentDto commentDto;
    private PostRedis postRedis;
    private Post post;

    @BeforeEach
    void setUp() {
        commentEvent = new CommentEvent(100L, 200L, 300L, 400L, null);
        comment = new Comment();
        commentDto = new CommentDto();
        commentDto.setId(999L);
        postRedis = PostRedis.builder()
                .id(100L)
                .authorId(200L)
                .projectId(300L)
                .content("Test content")
                .likesIds(List.of())
                .commentsIds(new ArrayList<>(List.of(10L, 20L, 30L)))
                .build();
        post = new Post();
    }

    @Test
    void handle_WhenCommentExists_ShouldProcessSuccessfully() {
        lock = mock(Lock.class);
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);

        when(commentRepository.findById(400L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(postRedisRepository.findById(100L)).thenReturn(Optional.of(postRedis));

        kafkaCommentEventHandler.handle(commentEvent, acknowledgment);

        verify(commentRepository).findById(400L);
        verify(commentMapper).toDto(comment);
        verify(postRedisRepository).findById(100L);
        verify(postRedisRepository).save(postRedis);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handle_WhenCommentNotFound_ShouldThrowException() {
        lock = mock(Lock.class);
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        when(commentRepository.findById(400L)).thenReturn(Optional.empty());

        kafkaCommentEventHandler.handle(commentEvent, acknowledgment);

        verify(commentRepository).findById(400L);
        verifyNoInteractions(postRedisRepository, acknowledgment);
    }

    @Test
    void handle_WhenPostNotFound_ShouldFetchFromDBAndSaveToRedis() {
        lock = mock(Lock.class);
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        when(commentRepository.findById(400L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(postRedisRepository.findById(100L)).thenReturn(Optional.empty());
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(postRedisMapper.toPostCache(post)).thenReturn(postRedis);

        kafkaCommentEventHandler.handle(commentEvent, acknowledgment);

        verify(postRepository).findById(100L);
        verify(postRedisMapper).toPostCache(post);
        verify(postRedisRepository).save(postRedis);
    }

    @Test
    void handle_WhenLockNotAcquired_ShouldNotProcess() {
        lock = mock(Lock.class);
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(false);

        kafkaCommentEventHandler.handle(commentEvent, acknowledgment);

        verifyNoInteractions(commentRepository, postRedisRepository, acknowledgment);
    }

    @Test
    void handle_WhenProcessingFails_ShouldNotAcknowledge() {
        lock = mock(Lock.class);
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        when(commentRepository.findById(400L)).thenThrow(new RuntimeException("Database error"));

        kafkaCommentEventHandler.handle(commentEvent, acknowledgment);

        verify(commentRepository).findById(400L);
        verifyNoInteractions(acknowledgment);
    }

    @Test
    void addComment_ShouldAddCommentAndMaintainMaxSize() {
        kafkaCommentEventHandler.addComment(postRedis, commentDto);

        assertEquals(3, postRedis.getCommentsIds().size());
        assertEquals(commentDto.getId(), postRedis.getCommentsIds().get(2));
    }
}
