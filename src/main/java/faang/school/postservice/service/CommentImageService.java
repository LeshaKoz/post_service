package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.model.Comment;

import faang.school.postservice.service.util.ImageResizer;
import faang.school.postservice.validation.CommentValidator;
import faang.school.postservice.validation.ValidateImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * Сервис для управления изображениями комментариев.
 * Обеспечивает загрузку, обработку и удаление изображений, прикрепленных к комментариям.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #uploadImage(Long, Long, MultipartFile)} - Загрузка и обработка изображения</li>
 *   <li>{@link #deleteImage(Long, Long)} - Удаление прикрепленных изображений</li>
 * </ul>
 *
 * @see CommentService
 * @see ImageResizer
 * @see GlobalExceptionHandler
 *
 *  * @author Zhltsk-V
 *  * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentImageService {

    private final CommentService commentService;
    private final AmazonS3 amazonS3;
    private final ImageResizer imageResizer;
    private final CommentValidator commentValidator;
    private final ValidateImage validateImage;

    @Value("${services.s3.bucketName.name}")
    private String bucketName;

    /**
     * Загружает изображение для указанного комментария.
     * Создает две версии изображения (большую и маленькую) и сохраняет их в S3-совместимом хранилище.
     *
     * @param postId    ID поста, содержащего комментарий
     * @param commentId ID комментария для прикрепления изображения
     * @param file      загружаемый файл изображения (максимум 5MB)
     * @return DTO обновленного комментария с ключами изображений
     * @throws DataValidationException  если файл превышает 5MB или не является изображением
     * @throws EntityNotFoundException  если комментарий или пост не существуют
     * @throws ImageProcessingException при ошибках обработки изображения
     */
    @Transactional
    public CommentViewDto uploadImage(Long postId, Long commentId, MultipartFile file) {
        Comment comment = commentService.getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);
        validateImage.validateImageFile(file);

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new DataValidationException("Uploaded file is not a valid image");
            }

            ProcessedImages images = processImages(originalImage);
            String baseKey = generateFileKeys(commentId);

            uploadImagesToStorage(baseKey, images);
            updateCommentWithImageKeys(comment, baseKey);

            return commentService.updateCommentEntity(comment);

        } catch (IOException e) {
            throw new ImageProcessingException("Failed to process image for comment ID: " + commentId, e);
        }
    }

    /**
     * Обрабатывает изображение, создавая уменьшенные версии.
     *
     * @param originalImage исходное изображение
     * @return контейнер с обработанными изображениями
     */
    private ProcessedImages processImages(BufferedImage originalImage) {
        return new ProcessedImages(
                imageResizer.resize(originalImage, 1080),
                imageResizer.resize(originalImage, 170)
        );
    }

    /**
     * Генерирует уникальные ключи для файлов.
     *
     * @param commentId ID комментария
     * @return базовый ключ для файлов
     */
    private String generateFileKeys(Long commentId) {
        return "comments/" + commentId + "/" + UUID.randomUUID();
    }

    /**
     * Загружает изображения в хранилище.
     *
     * @param baseKey базовый ключ
     * @param images  обработанные изображения
     */
    private void uploadImagesToStorage(String baseKey, ProcessedImages images) throws IOException {
        uploadToS3(baseKey + "-large.jpg", images.largeImage());
        uploadToS3(baseKey + "-small.jpg", images.smallImage());
    }

    /**
     * Обновляет комментарий с ключами изображений.
     *
     * @param comment комментарий для обновления
     * @param baseKey базовый ключ
     */
    private void updateCommentWithImageKeys(Comment comment, String baseKey) {
        comment.setLargeImageFileKey(baseKey + "-large.jpg");
        comment.setSmallImageFileKey(baseKey + "-small.jpg");
    }

    /**
     * Удаляет изображения, прикрепленные к комментарию.
     *
     * @param postId    ID поста
     * @param commentId ID комментария
     * @return DTO обновленного комментария
     * @throws EntityNotFoundException если комментарий или пост не существуют
     */
    @Transactional
    public CommentViewDto deleteImage(Long postId, Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);

        deleteImagesFromStorage(comment);
        clearImageReferences(comment);

        return commentService.updateCommentEntity(comment);
    }

    /**
     * Удаляет изображения из хранилища.
     *
     * @param comment комментарий с изображениями
     */
    //todo: позволяет удалять два и более раз, добавить проверку на существование файла

    private void deleteImagesFromStorage(Comment comment) {
        try {
            if (comment.getLargeImageFileKey() != null) {
                amazonS3.deleteObject(bucketName, comment.getLargeImageFileKey());
            }
            if (comment.getSmallImageFileKey() != null) {
                amazonS3.deleteObject(bucketName, comment.getSmallImageFileKey());
            }
        } catch (AmazonS3Exception e) {
            log.warn("Failed to delete image files for comment ID: {}", comment.getId(), e);
        }
    }

    /**
     * Очищает ссылки на изображения в комментарии.
     *
     * @param comment комментарий для очистки
     */
    private void clearImageReferences(Comment comment) {
        comment.setLargeImageFileKey(null);
        comment.setSmallImageFileKey(null);
    }

    /**
     * Внутренний метод для загрузки изображения в хранилище.
     *
     * @param key   ключ для сохранения
     * @param image изображение для загрузки
     * @throws IOException при ошибках ввода-вывода
     */
    private void uploadToS3(String key, BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(os.size());
        metadata.setContentType("image/jpeg");

        amazonS3.putObject(bucketName, key, new ByteArrayInputStream(os.toByteArray()), metadata);
    }

    /**
     * Вспомогательный класс для хранения обработанных изображений.
     */
    private record ProcessedImages(BufferedImage largeImage, BufferedImage smallImage) {
    }
}