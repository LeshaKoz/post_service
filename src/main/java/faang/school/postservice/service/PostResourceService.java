package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.utilities.ImageResizer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostResourceService {

    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final ImageResizer imageResizer;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;
    @Value("${services.s3.max-images-count-for-post}")
    private int maxCountImagesForPost;
    @Value("${services.s3.max-width-horizontal-image}")
    private int maxWidthHorizontalImage;
    @Value("${services.s3.max-height-horizontal-image}")
    private int maxHeightHorizontalImage;
    @Value("${services.s3.max-side-square-image}")
    private int maxSideSquareImage;

    public ResourceDto addPostImage(Long postId, MultipartFile image) {
        Post post = getPostOrThrow(postId);

        MultipartFile processedImage = resizeOrThrow(image);

        if (post.getFileKeys().size() >= maxCountImagesForPost) {
            throw new IllegalArgumentException(
                    String.format("В одном посте может быть максимум %d изображений", maxCountImagesForPost));
        }

        String folder = "Post" + postId;
        String key = s3Service.uploadFile(processedImage, folder);

        post.getFileKeys().add(key);
        postRepository.save(post);

       ResourceDto resourceDto = ResourceDto.builder()
                .key(key)
                .name(image.getOriginalFilename())
                .type(image.getContentType())
                .size(processedImage.getSize())
               .postId(post.getId())
               .createdAt(LocalDateTime.now())
                .build();
        return resourceDto;
    }

    @Transactional
    public String deleteImageByKey(Long postId, String key) {
        Post post = getPostOrThrow(postId);
        if (!post.getFileKeys().remove(key)) {
            throw new EntityNotFoundException("Изображение с ключом " + key + " не найдено в посте " + postId);
        }
        postRepository.save(post);
        s3Service.deleteFile(key);
        return key;
    }

    public byte[] getImageByKey(String key) {
        try (InputStream inputStream = s3Service.downloadFile(key)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Ошибка обработки файла", e);
            throw new RuntimeException("Ошибка обработки файла", e);
        }
    }

    public List<String> getAllImageKeysByPostId(Long postId) {
        Post post = getPostOrThrow(postId);
        return post.getFileKeys();
    }

    @Transactional
    public void deleteAllImagesByPostId(Long postId) {
        Post post = getPostOrThrow(postId);
        List<String> keysToDelete = new ArrayList<>(post.getFileKeys());
        post.getFileKeys().clear();
        postRepository.save(post);
        keysToDelete.forEach(s3Service::deleteFile);
    }

    private Post getPostOrThrow(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден с ID: " + postId));
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Пост с id " + postId + " удалён");
        }
        return post;
    }

    private MultipartFile resizeOrThrow(MultipartFile image) {
        MultipartFile resultImage = image;
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Загруженный файл не является изображением.");
            }
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if (width > height) {
                if (width > maxWidthHorizontalImage || height > maxHeightHorizontalImage) {
                    resultImage = imageResizer.resizeImage(image, maxWidthHorizontalImage, maxHeightHorizontalImage);
                }
            }

            else if (width == height) {
                if (width > maxSideSquareImage) {
                    resultImage = imageResizer.resizeImage(image, maxSideSquareImage, maxSideSquareImage);
                }
            }

            else {
                throw new IllegalArgumentException("Вертикальные изображения не поддерживаются.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка обработки изображения", e);
        }

        if (image.getSize() > maxImageSizeMb * 1024 * 1024) {
            throw new IllegalArgumentException("Размер изображения не должен превышать " + maxImageSizeMb + " МБ.");
        }
        return resultImage;
    }
}