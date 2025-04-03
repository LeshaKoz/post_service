package faang.school.postservice.CommentService;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.dto.commentAnalyzer.response.AttributeScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.SpanScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.SummaryScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import faang.school.postservice.enums.CommentToxicityType;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentAnalyzer commentAnalyzer;

    private Mono<ToxicityScoreDto> toxicityScore;
    private final ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    private final int commentModerationBatchSize = 3;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(commentService, "banBatchSize", 100);
        ReflectionTestUtils.setField(commentService, "commentModerationTimeoutHours", 1);
        ReflectionTestUtils.setField(commentService, "commentModerationBatchSize", commentModerationBatchSize);

        toxicityScore = Mono.just(ToxicityScoreDto.builder()
                .attributeScores(Map.of(
                        CommentToxicityType.TOXICITY, new AttributeScoreDto(
                                List.of(new SpanScoreDto(new SummaryScoreDto(0.15, "PROBABILITY"))),
                                new SummaryScoreDto(0.15, "PROBABILITY")
                        ),
                        CommentToxicityType.INSULT, new AttributeScoreDto(
                                List.of(new SpanScoreDto(new SummaryScoreDto(0.05, "PROBABILITY"))),
                                new SummaryScoreDto(0.05, "PROBABILITY")
                        )
                ))
                .languages(List.of("en"))
                .detectedLanguages(List.of("en"))
                .build());
    }

    @Test
    public void testModerateComments_moderationPassed() {
        Comment comment1 = Comment.builder().content("content1").build();
        Comment comment2 = Comment.builder().content("content2").build();
        Comment comment3 = Comment.builder().content("content2").build();

        List<Comment> comments = List.of(comment1, comment2, comment3);
        Page<Comment> commentPage = new PageImpl<>(comments, PageRequest.of(0, commentModerationBatchSize), 3);

        when(commentRepository.count()).thenReturn(3L);
        when(commentRepository.findComments(any())).thenReturn(commentPage);
        when(commentAnalyzer.analyzeComment(anyString())).thenReturn(toxicityScore);

        Mono<Void> result = commentService.moderateComments();
        StepVerifier.create(result)
                .verifyComplete();

        verify(commentRepository, times(3)).save(commentCaptor.capture());
        List<Comment> capturedComments = commentCaptor.getAllValues();
        assertEquals(3, capturedComments.size());
        assertTrue(capturedComments.containsAll(comments));
        assertTrue(capturedComments.stream().allMatch(Comment::isVerified));
    }

    @Test
    public void testModerateComments_moderationFailed() {
        Comment comment1 = Comment.builder().content("This text contains offensive content").build();
        toxicityScore.block().getAttributeScores().get(CommentToxicityType.TOXICITY)
                .setSummaryScore(new SummaryScoreDto(0.45, "PROBABILITY"));

        List<Comment> comments = List.of(comment1);
        Page<Comment> commentPage = new PageImpl<>(comments, PageRequest.of(0, 1), 1);

        when(commentRepository.count()).thenReturn(1L);
        when(commentRepository.findComments(any())).thenReturn(commentPage);
        when(commentAnalyzer.analyzeComment(anyString())).thenReturn(toxicityScore);

        Mono<Void> result = commentService.moderateComments();
        StepVerifier.create(result)
                .verifyComplete();

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        List<Comment> capturedComments = commentCaptor.getAllValues();
        assertEquals(1, capturedComments.size());
        assertTrue(capturedComments.containsAll(comments));
        assertFalse(capturedComments.stream().allMatch(Comment::isVerified));
    }
}
