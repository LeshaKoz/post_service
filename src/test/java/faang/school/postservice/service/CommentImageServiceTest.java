package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.util.ImageResizer;
import faang.school.postservice.validation.CommentValidator;
import faang.school.postservice.validation.ValidateImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса изображений комментариев")
public class CommentImageServiceTest {

    @Mock
    private CommentService commentService;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private ImageResizer imageResizer;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private ValidateImage validateImage;

    @InjectMocks
    private CommentImageService commentImageService;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private MockMultipartFile imageFile;
    private Comment comment;
    private CommentViewDto commentViewDto;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() throws IOException {
        imageFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]);

        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        Post post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(commentId);
        commentViewDto.setPostId(postId);

        ReflectionTestUtils.setField(commentImageService, "bucketName", "test-bucket");
    }

    @Nested
    @DisplayName("Загрузка изображения")
    class UploadImage {

        @DisplayName("Успешная загрузка и обработка изображения")
        void givenValidImage_whenUploadImage_thenProcessAndSaveImages() throws IOException {
            MultipartFile imageFile = mock(MultipartFile.class);

            when(imageFile.isEmpty()).thenReturn(false);
            when(imageFile.getSize()).thenReturn(1024L); // Размер меньше 5MB
            when(imageFile.getContentType()).thenReturn("image/jpeg");
            when(imageFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(imageResizer.resize(any(), eq(1080))).thenReturn(testImage);
            when(imageResizer.resize(any(), eq(170))).thenReturn(testImage);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentImageService.uploadImage(postId, commentId, imageFile);

            assertNotNull(result);
            verify(validateImage).validateImageFile(imageFile);
            verify(commentValidator).validateCommentBelongsToPost(comment, postId);
            verify(amazonS3, times(2)).putObject(anyString(), anyString(), any(), any());
            assertNotNull(comment.getLargeImageFileKey());
            assertNotNull(comment.getSmallImageFileKey());
        }

        @Test
        @DisplayName("Ошибка при невалидном изображении")
        void givenInvalidImage_whenUploadImage_thenThrowException() throws IOException {
            MockMultipartFile invalidImage = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", new byte[0]
            );

            assertThrows(DataValidationException.class,
                    () -> commentImageService.uploadImage(postId, commentId, invalidImage));
        }

        @Test
        @DisplayName("Ошибка при обработке изображения")
        void givenCorruptedImage_whenUploadImage_thenThrowException() {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            doThrow(new DataValidationException("Invalid image")).when(validateImage).validateImageFile(imageFile);

            assertThrows(DataValidationException.class,
                    () -> commentImageService.uploadImage(postId, commentId, imageFile));
        }
    }

    @Nested
    @DisplayName("Удаление изображения")
    class DeleteImage {

        @Test
        @DisplayName("Успешное удаление изображения")
        void givenCommentWithImages_whenDeleteImage_thenRemoveImages() {
            comment.setLargeImageFileKey("large-key");
            comment.setSmallImageFileKey("small-key");

            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentImageService.deleteImage(postId, commentId);

            assertNotNull(result);
            verify(amazonS3).deleteObject("test-bucket", "large-key");
            verify(amazonS3).deleteObject("test-bucket", "small-key");
            assertNull(comment.getLargeImageFileKey());
            assertNull(comment.getSmallImageFileKey());
        }

        @Test
        @DisplayName("Удаление комментария без изображений")
        void givenCommentWithoutImages_whenDeleteImage_thenSuccess() {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            assertDoesNotThrow(() -> commentImageService.deleteImage(postId, commentId));
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_whenDeleteImage_thenThrowException() {
            when(commentService.getCommentById(commentId)).thenThrow(
                    new EntityNotFoundException("Comment not found"));

            assertThrows(EntityNotFoundException.class,
                    () -> commentImageService.deleteImage(postId, commentId));
        }
    }
}
