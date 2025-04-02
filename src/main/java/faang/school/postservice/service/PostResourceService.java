package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.ResourcePostIdNotEqualsPostIdException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.exception.not_found_exceptions.PostNotFoundException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.minio.MinioConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostResourceService {
    private static final Long BYTES_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Integer MAX_COUNT_OF_RESOURCES = 10;
    private static final int STANDARD_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;
    private static final int VERTICAL_SQUARE_HEIGHT = 1080;
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final ResourceMapper resourceMapper;
    private final MinioConfig minioConfig;

    public ResourceDto add(Long postId, List<MultipartFile> files) {
        files.forEach(this::validateFile);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));

        Resource resource = null;

        for (MultipartFile file : files) {
            if (post.getResources().size() == MAX_COUNT_OF_RESOURCES) {
                log.error(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
                throw new MaxResourcesReachedException(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
            }

            String key = file.getOriginalFilename() + System.currentTimeMillis();
            String fileExtension = file.getOriginalFilename().
                    substring(file.getOriginalFilename().lastIndexOf(".") + 1);


            try {
                if (file.getContentType().startsWith("image")) {
                    BufferedImage croppedImage = resizeImage(file);
                    log.info("The file has been resized");
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(croppedImage, fileExtension, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();
                    InputStream inputStream = new ByteArrayInputStream(imageBytes);

                    resource = minioConfig.uploadImage(inputStream, imageBytes, key, file.getName(), file.getContentType());
                }

                if (file.getContentType().startsWith("video") || file.getContentType().startsWith("audio")) {
                    resource = minioConfig.uploadVideoOrAudio(file, key);
                }

                resource.setPost(post);
                post.getResources().add(resource);
                postResourceRepository.save(resource);
            } catch (IOException e) {
                log.error(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
                throw new MinioUploadingFileException(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
            }
        }

        postRepository.save(post);

        if (resource == null) {
            throw new RuntimeException();
        }

        return resourceMapper.toResourceDto(resource);
    }

    public void delete(Long postId, Long resourceId) {
        Resource resource = postResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND_EXCEPTION));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));
        String key = resource.getKey();

        if (!postId.equals(resource.getPost().getId())) {
            log.error(ExceptionMessages.RESOURCE_POST_ID_NOT_EQUALS_POST_ID_EXCEPTION);
            throw new ResourcePostIdNotEqualsPostIdException(
                    ExceptionMessages.RESOURCE_POST_ID_NOT_EQUALS_POST_ID_EXCEPTION
            );
        }

        post.getResources().remove(resource);
        postRepository.save(post);
        minioConfig.delete(key);
        postResourceRepository.deleteById(resourceId);
        log.info("The file has been removed from the bucket");
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        boolean isHorizontal = image.getWidth() > image.getHeight();

        int imageWidth = STANDARD_WIDTH;
        int imageHeight = isHorizontal ? HORIZONTAL_HEIGHT : VERTICAL_SQUARE_HEIGHT;

        if (image.getWidth() <= imageWidth && image.getHeight() <= imageHeight) {
            return image;
        }

        return Thumbnails.of(image)
                .size(imageWidth, imageHeight)
                .keepAspectRatio(false)
                .asBufferedImage();
    }

    private void validateFile(MultipartFile file) {
        if (file.getName().isBlank()) {
            log.error(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
        }

        if (file.getSize() > BYTES_FILE_SIZE) {
            log.error(ExceptionMessages.FILE_SIZE_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_SIZE_EXCEPTION);
        }
    }
}
