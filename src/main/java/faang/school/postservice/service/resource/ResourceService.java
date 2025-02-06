package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceResponse;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.utils.ImageService;
import faang.school.postservice.utils.MinioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private static final String BUCKET_NAME = "post-images";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ImageService imageService;
    private final MinioService minioService;

    public List<ResourceResponse> addFilesToPost(long postId, List<MultipartFile> files) {
        log.info("Вызван addFilesToPost, postId = {}, files.size = {}", postId, files.size());
        if (files.isEmpty() || files.size() > 10) {
            log.error("Можно загрузить не более 10 изображений и не менее 1 изображения");
            throw new IllegalStateException("Можно загрузить не более 10 изображений и не менее 1 изображения");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post не найден"));

        return files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> fileUploadResult(file, post)))
                .map(CompletableFuture::join)
                .map(resourceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ResourceResponse addFileToPost(long postId, MultipartFile file) {
        log.info("Вызван addFileToPost, postId = {}, fileName = {}", postId, file.getOriginalFilename());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post не найден"));

        int filesCount = resourceRepository.findByPostId(postId).size();
        if (filesCount >= 10) {
            log.error("Можно загрузить не более 10 изображений.");
            throw new IllegalStateException("Можно загрузить не более 10 изображений.");
        }

        Resource resource = fileUploadResult(file, post);
        return resourceMapper.toResponse(resource);
    }

    /**
     * Метод выполняет загрузку файла:
     * 1. Проверяет, что размер файла не превышает 5 МБ.
     * 2. Если файл является изображением, проверяет его разрешение и при необходимости вызывает imageService.saveImage,
     *    который выполняет ресайз и загрузку в Minio.
     * 3. Если изменение размера не требуется, файл загружается напрямую через minioService.
     * 4. Создаётся объект Resource, в котором сохраняется id файла (поле key) из Minio.
     */
    private Resource fileUploadResult(MultipartFile file, Post post) {
        // Проверка размера файла
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("Размер файла {} превышает допустимый лимит в 5 МБ", file.getOriginalFilename());
            throw new FileException("Размер файла не должен превышать 5 МБ");
        }

        String uploadedFileId;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            // Если файл является изображением
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();

                // Если изображение превышает заданные лимиты:
                // горизонтальное – ширина больше 1080 и высота больше 566, либо квадратное – больше 1080
                if ((width != height && width > 1080 && height > 566) || (width == height && width > 1080)) {
                    log.info("Изображение {} превышает заданные разрешения, выполняется ресайз", file.getOriginalFilename());
                    uploadedFileId = imageService.saveImage(file, 1080, BUCKET_NAME);
                } else {
                    uploadedFileId = minioService.uploadFile(file.getBytes(), file.getContentType(), BUCKET_NAME);
                }
            } else {
                uploadedFileId = minioService.uploadFile(file.getBytes(), file.getContentType(), BUCKET_NAME);
            }
        } catch (IOException e) {
            log.error("Ошибка обработки изображения {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new FileException("Ошибка обработки изображения");
        }

        // Генерируем объект Resource и сохраняем информацию о файле в базе (например, оригинальное имя, размер, тип и id файла из Minio)
        Resource resource = generateResource(file, uploadedFileId, post);
        return resourceRepository.save(resource);
    }

    /**
     * Генерирует объект Resource для сохранения в БД.
     * Поле key содержит идентификатор файла, полученный после загрузки в Minio.
     */
    private Resource generateResource(MultipartFile multipartFile, String fileKey, Post post) {
        log.info("Генерация нового ресурса для файла {}", multipartFile.getOriginalFilename());
        Resource resource = new Resource();
        resource.setKey(fileKey);
        resource.setName(multipartFile.getOriginalFilename());
        resource.setSize(multipartFile.getSize());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setType(multipartFile.getContentType());
        resource.setPost(post);
        return resource;
    }

    public List<InputStream> getFilesForPost(long postId) {
        // Получаем список ресурсов для поста из БД
        List<Resource> resources = resourceRepository.findByPostId(postId);

        // Для каждого ресурса получаем файл из Minio по ключу, оборачиваем в ByteArrayInputStream и возвращаем список потоков
        return resources.stream()
                .map(resource -> {
                    byte[] fileBytes = minioService.getFile("post-images", resource.getKey());
                    return new ByteArrayInputStream(fileBytes);
                })
                .collect(Collectors.toList());
    }

}
