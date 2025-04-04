package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    @InjectMocks
    private CommentServiceImpl commentService;

    private final Executor synchronousExecutor = Runnable::run;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(commentService, "commentModeratorExecutor", synchronousExecutor);
        ReflectionTestUtils.setField(commentService, "limit", 20);
    }

    @Test
    public void moderateCommentsTest(){
        List<Long> commentIds = LongStream.range(1, 101)
                .boxed()
                .toList();

        List<Comment> comments = commentIds.stream()
                .map(id -> Comment.builder().id(id).content("Some content").build())
                .toList();

        when(moderationDictionary.isTextAreCorrect(anyString())).thenReturn(true);
        when(commentRepository.getUnverifiedCommentsIds()).thenReturn(commentIds);
        when(commentRepository.getUnverifiedComments(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> ids = invocation.getArgument(0);
                    return comments.stream()
                            .filter(c -> ids.contains(c.getId()))
                            .toList();
                });
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        commentService.moderateComments();

        verify(commentRepository, times(5)).getUnverifiedComments(anyList());
        verify(commentRepository, times(comments.size())).save(any(Comment.class));

        comments.forEach(comment -> assertTrue(comment.getVerified()));
    }
}