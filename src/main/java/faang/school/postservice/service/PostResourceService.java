package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.FileEmptyException;
import faang.school.postservice.exception.FileSizeException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.MinioUploadingFileException;
import faang.school.postservice.exception.NotImageFileException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.ResourceNotFoundException;
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
    private final static Integer MAX_COUNT_OF_RESOURCES = 10;
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final ResourceMapper resourceMapper;
    private final ExceptionMessages exceptionMessages;
    private final MinioConfig minioConfig;

    public ResourceDto add(Long postId, List<MultipartFile> files) {
        files.forEach(this::validateFile);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(exceptionMessages.postNotFoundException));

        Resource resource = null;

        for (MultipartFile file : files) {
            if (post.getResources().size() == MAX_COUNT_OF_RESOURCES) {
                log.error(exceptionMessages.resourceMaxLimitException);
                throw new MaxResourcesReachedException(exceptionMessages.resourceMaxLimitException);
            }

            System.out.println("ssssssssssssssssize");
            System.out.println(post.getResources().size());

            String key = file.getOriginalFilename() + System.currentTimeMillis();
            String fileExtension = file.getOriginalFilename().
                    substring(file.getOriginalFilename().lastIndexOf(".") + 1);


            try {
                BufferedImage croppedImage = resizeImage(file);
                log.info("The file has been resized");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(croppedImage, fileExtension, outputStream);
                byte[] imageBytes = outputStream.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(imageBytes);

                resource = minioConfig.uploadFile(inputStream, imageBytes, key, file.getName());
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
                .orElseThrow(() -> new ResourceNotFoundException(exceptionMessages.resourceNotFoundException));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(exceptionMessages.postNotFoundException));
        String key = resource.getKey();

        post.getResources().remove(resource);
        postRepository.save(post);
        minioConfig.delete(key);
        postResourceRepository.deleteById(resourceId);
        log.info("The file has been removed from the bucket");
    }

    private void validateFile(MultipartFile file) {
        if (!file.getContentType().startsWith("image")) {
            log.info(exceptionMessages.notImageFileException);
            throw new NotImageFileException(exceptionMessages.notImageFileException);
        }

        if (file.isEmpty()) {
            log.info(exceptionMessages.fileEmptyException);
            throw new FileEmptyException(exceptionMessages.fileEmptyException);
        }

        if (file.getSize() > BYTES_FILE_SIZE) {
            log.error(exceptionMessages.resourceMaxLimitException);
            throw new FileSizeException(exceptionMessages.resourceMaxLimitException);
        }
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        boolean isHorizontal = image.getWidth() > image.getHeight();

        int imageWidth = 1080;
        int imageHeight = isHorizontal ? 566 : 1080;

        if (image.getWidth() <= imageWidth && image.getHeight() <= imageHeight) {
            return image;
        }

        return Thumbnails.of(image)
                .size(imageWidth, imageHeight)
                .asBufferedImage();
    }
}
