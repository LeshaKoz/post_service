package faang.school.postservice.validator;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.validator.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeRepository likeRepository;
    @InjectMocks
    private LikeValidator likeValidator;
    private LikeDto likeDto = LikeDto.builder()
            .postId(1L)
            .commentId(1L)
            .userId(1L)
            .build();

    @Test
    public void testCheckLikeFieldsDto_WithPostIdAndCommentIdThrows() {
        BusinessException businessException = Assertions.assertThrows(BusinessException.class, () -> {
            likeValidator.checkLikeFieldsDto(likeDto);
        });

        Assertions.assertEquals(businessException.getMessage(),
                "Лайк может быть связан только с одним объектом: постом или комментарием.");
    }

    @Test
    public void testVerifyPostLikeExistsThrows() throws NoSuchMethodException {
        when(likeRepository.findLikeByPostIdAndUserId(likeDto.postId(),
                likeDto.userId())).thenReturn(Optional.of(new Like()));

        Method method = LikeValidator.class.getDeclaredMethod("verifyPostLikeExists", LikeDto.class);
        method.setAccessible(true);

        BusinessException businessException = Assertions.assertThrows(BusinessException.class, () -> {
            try {
                method.invoke(likeValidator, likeDto);
            } catch (InvocationTargetException e) {
                throw (BusinessException) e.getCause(); // Извлекаем фактическое исключение
            }
        });


        Assertions.assertEquals(businessException.getMessage(),
                "Нельзя повторно ставить лайк на пост с ID " + likeDto.postId());
    }

    @Test
    public void testVerifyCommentLikeExistsThrows() throws NoSuchMethodException {
        when(likeRepository.findLikeByCommentIdAndUserId(likeDto.commentId(),
                likeDto.userId())).thenReturn(Optional.of(new Like()));

        Method method = LikeValidator.class.getDeclaredMethod("verifyCommentLikeExists", LikeDto.class);
        method.setAccessible(true);

        BusinessException businessException = Assertions.assertThrows(BusinessException.class, () -> {
            try {
                method.invoke(likeValidator, likeDto);
            } catch (InvocationTargetException e) {
                throw (BusinessException) e.getCause(); // Извлекаем фактическое исключение
            }
        });

        Assertions.assertEquals(businessException.getMessage(),
                "Нельзя повторно ставить лайк на комментарий с ID " + likeDto.commentId());
    }

    @Test
    public void testFindPostByIdThrows() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeValidator.findPostById(postId);
        });

        Assertions.assertEquals(exception.getMessage(), "Пост с ID " + postId + " не найден");
    }

    @Test
    public void testFindCommentByIdThrows() {
        long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeValidator.findCommentById(commentId);
        });

        Assertions.assertEquals(exception.getMessage(), "Комментарий с ID " + commentId + " не найден");
    }

    @Test
    public void testCheckLikeBeforeDeleteWithPostNotNull() {
        LikeDto dto = LikeDto.builder()
                .postId(1L)
                .userId(1L)
                .build();
        when(likeRepository.findLikeByPostIdAndUserId(dto.postId(), dto.userId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeValidator.checkLikeBeforeDelete(dto);
        });

        Assertions.assertEquals(exception.getMessage(), "Лайк к посту с ID " + dto.postId() + " не найден");
    }

    @Test
    public void testCheckLikeBeforeDeleteWithCommentNotNull() {
        LikeDto dto = LikeDto.builder()
                .commentId(1L)
                .userId(1L)
                .build();
        when(likeRepository.findLikeByCommentIdAndUserId(dto.commentId(), dto.userId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeValidator.checkLikeBeforeDelete(dto);
        });

        Assertions.assertEquals(exception.getMessage(), "Лайк к комментарию с ID " + dto.commentId() + " не найден");
    }

}
