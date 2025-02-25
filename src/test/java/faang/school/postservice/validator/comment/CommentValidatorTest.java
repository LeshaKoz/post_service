package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Spy
    private final CommentValidator commentValidator = new CommentValidator();

    @Test
    @DisplayName("Позитивный тест метода positiveValidComment")
    void testPositiveValidComment() {
        Comment comment = Comment.builder().id(1L).authorId(1L).post(Post.builder().id(1L).build()).build();
        CommentDto commentDto = CommentDto.builder().id(1L).postId(1L).content("cont").build();
        assertThrows(IllegalArgumentException.class, () -> commentValidator.validComment(comment, commentDto));
    }

    @Test
    @DisplayName("Негативный тест метода negativeValidComment")
  void testNegativeValidComment() {
     Comment comment = Comment.builder().id(1L).authorId(1L).content("cont").post(Post.builder().id(1L).build()).build();
        CommentDto commentDto = CommentDto.builder().id(1L).authorId(1L).postId(1L).content("cont1").build();
        assertThatCode(() -> commentValidator.validComment(comment, commentDto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Позитивный тест метода positiveValidPost")
    void testPositiveValidPost() {
        Post post = Post.builder().id(1L).comments(
                List.of(
                        Comment.builder().content("Hi").build(),
                        Comment.builder().content("23").build()
                )).build();
        assertThatCode(() -> commentValidator.validPostComments(post)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Негативный тест метода negativeValidPost")
    void testNegativeValidPost() {
        Post post = Post.builder().id(1L).comments(new ArrayList<>()).build();
        assertThrows(IllegalArgumentException.class, () -> commentValidator.validPostComments(post));
    }
}
