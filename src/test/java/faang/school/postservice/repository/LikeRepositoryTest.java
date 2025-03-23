package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeRepositoryTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private Post post;

    @Mock
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByPostId() {
        // Подготавливаем тестовые данные с помощью Builder
        long postId = 1L;

        Post post = Post.builder()
                .id(postId)
                .content("Sample Post")
                .authorId(1L)
                .published(true)
                .deleted(false)
                .build();

        // Создаем объект Like для поста с помощью Builder
        Like like = Like.builder()
                .userId(1L)
                .post(post)  // Пост не null
                .build();  // Закрываем билд

        // Мокируем поведение репозитория
        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like));

        // Вызываем метод репозитория
        List<Like> likes = likeRepository.findByPostId(postId);

        // Проверяем, что результат правильный
        assertNotNull(likes);
        assertEquals(1, likes.size());
    }

    @Test
    void testFindByCommentId() {
        // Подготавливаем тестовые данные с помощью Builder
        long commentId = 1L;

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Sample Comment")
                .authorId(1L)
                .build();

        // Создаем объект Like для комментария с помощью Builder
        Like like = Like.builder()
                .userId(1L)
                .comment(comment)  // Комментарий не null
                .build();  // Закрываем билд

        // Мокируем поведение репозитория
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like));

        // Вызываем метод репозитория
        List<Like> likes = likeRepository.findByCommentId(commentId);

        // Проверяем, что результат правильный
        assertNotNull(likes);
        assertEquals(1, likes.size());
    }
}
