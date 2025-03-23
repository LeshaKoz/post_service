package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("getComment(). Positive")
    void getCommentPositive() {
        Long commentId = 1234567L;
        Comment comment = new Comment();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment returnedComment = commentService.getComment(commentId);

        assertEquals(comment, returnedComment);
        Mockito.verify(commentRepository, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("getComment(). Negative")
    void getCommentNegative() {
        Long commentId = 1234567L;
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.getComment(commentId));
        Mockito.verify(commentRepository, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("getComments()")
    void getComments() {
        Long postId = 1234567L;
        List<Comment> postsSend = List.of(
                Comment.builder().id(1L).build(),
                Comment.builder().id(2L).build(),
                Comment.builder().id(3L).build());
        List<Comment> postsExpected = List.of(
                Comment.builder().id(3L).build(),
                Comment.builder().id(2L).build(),
                Comment.builder().id(1L).build());

        Mockito.when(commentRepository.findAllByPostId(postId)).thenReturn(postsSend);

        assertEquals(postsExpected, commentService.getComments(postId));
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByPostId(any(Long.class));
    }

    @Test
    void createComment() {
        Long postId = 1234567L;
        Post post = Post.builder().id(postId).build();
        Long newId = null;

        Mockito.when(postService.getPost(any(Long.class))).thenReturn(post);
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        commentService.createComment(postId, new Comment());

        Mockito.verify(postService, Mockito.times(1)).getPost(any(Long.class));
        Mockito.verify(commentRepository, Mockito.times(1)).save(commentCaptor.capture());
        Comment commentArgument = commentCaptor.getValue();
        assertEquals(newId, commentArgument.getId());
        assertEquals(postId, commentArgument.getPost().getId());

    }

    @Test
    @DisplayName("updateComment(). Positive")
    void updateCommentPositive() {
        Long commentId = 1234567L;
        Comment updatesForComment = Comment.builder().content("content").largeImageFileKey("large").smallImageFileKey("small").build();
        Comment originalComment = new Comment();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));

        Comment returnedComment = commentService.updateComment(commentId, updatesForComment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(any(Comment.class));
        assertEquals(updatesForComment.getContent(), returnedComment.getContent());
        assertEquals(updatesForComment.getLargeImageFileKey(), returnedComment.getLargeImageFileKey());
        assertEquals(updatesForComment.getSmallImageFileKey(), returnedComment.getSmallImageFileKey());
    }

    @Test
    @DisplayName("updateComment(). Negative. Update has not new information")
    void updateCommentNegative() {
        Long commentId = 1234567L;
        Comment updatesForComment = Comment.builder().content("content").largeImageFileKey("large").smallImageFileKey("small").build();
        Comment originalComment = Comment.builder().content("content").largeImageFileKey("large").smallImageFileKey("small").build();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));

        Comment returnedComment = commentService.updateComment(commentId, updatesForComment);

        Mockito.verify(commentRepository, Mockito.never()).save(any(Comment.class));
        assertEquals(updatesForComment.getContent(), returnedComment.getContent());
        assertEquals(updatesForComment.getLargeImageFileKey(), returnedComment.getLargeImageFileKey());
        assertEquals(updatesForComment.getSmallImageFileKey(), returnedComment.getSmallImageFileKey());
    }

    @Test
    @DisplayName("deleteComment(). Positive")
    void deleteComment() {
        Long commentId = 1234567L;
        Comment commentFromDB = new Comment();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentFromDB));

        assertTrue(commentService.deleteComment(commentId));
        Mockito.verify(commentRepository, Mockito.times(1)).delete(commentFromDB);
    }
    @Test
    @DisplayName("deleteComment(). Negative")
    void deleteCommentNegative() {
        Long commentId = 1234567L;

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId));
        Mockito.verify(commentRepository, Mockito.never()).delete(any());
    }
}