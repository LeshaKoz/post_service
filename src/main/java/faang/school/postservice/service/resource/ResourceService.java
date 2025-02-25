package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.MediaFileException;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.service.util.ImageResizer;
import faang.school.postservice.service.util.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final ImageResizer imageResizer;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;

    @Value("${resources.cleanup.retention-period}")
    private String retentionPeriod;

    @Transactional
    public List<ResourceDto> attachImages(Long postId, List<MultipartFile> imageFiles) {
        Post post = getPostById(postId);
        resourceValidator.validateImages(imageFiles, post.getResources());
        List<MultipartFile> resizedImages = resizeFiles(imageFiles);

        List<Resource> resources = s3Service.uploadFiles(
                resizedImages, createFolder(postId));
        resources.forEach(resource -> {
            resource.setPost(post);
            resourceRepository.save(resource);
        });

        post.getResources().addAll(resources);
        postRepository.save(post);

        return resourceMapper.toResourceDtoList(resources);
    }

    @Transactional
    public ResourceDto deleteImage(Long resourceId) {
        Resource resource = getResourceById(resourceId);

        if (resource.getStatus() == ResourceStatus.ACTIVE) {
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedAt(LocalDateTime.now());
            resourceRepository.save(resource);
        } else {
            throw new IllegalStateException("Resource is already in 'DELETED' status!");
        }

        return resourceMapper.toResourceDto(resource);
    }

    @Transactional
    public ResourceDto restoreImage(Long resourceId) {
        Resource resource = getResourceById(resourceId);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            resource.setStatus(ResourceStatus.ACTIVE);
            resource.setUpdatedAt(LocalDateTime.now());
            resourceRepository.save(resource);
        } else {
            throw new IllegalStateException("Resource is already in 'ACTIVE' status!");
        }

        return resourceMapper.toResourceDto(resource);
    }

    @Transactional
    @Scheduled(cron = "${resources.cleanup.scheduler.cron}")
    public void deleteOldDeletedResources() {
        LocalDateTime expirationPeriod = calculateRetentionDate();
        List<Resource> expiredDeletedResources =
                resourceRepository.findAllByStatusAndUpdatedAtBefore(ResourceStatus.DELETED, expirationPeriod);

        expiredDeletedResources
                .forEach(resource -> {
                    s3Service.deletedFile(resource.getKey());
                    resourceRepository.delete(resource);
                });
    }

    private List<MultipartFile> resizeFiles(List<MultipartFile> imageFiles) {
        List<MultipartFile> resizedFiles = new ArrayList<>();

        imageFiles.forEach(file -> {
            try {
                resizedFiles.add(imageResizer.resizeImage(file));
            } catch (IOException ex) {
                log.error(ex.getMessage());
                throw new MediaFileException("Failed to resize image: " + file.getOriginalFilename());
            }
        });
        return resizedFiles;
    }

    private String createFolder(Long postId) {
        return String.format("images_for_post_%d", postId);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    private LocalDateTime calculateRetentionDate() {
        if (retentionPeriod.endsWith("M")) {
            int months = Integer.parseInt(retentionPeriod.substring(0, retentionPeriod.length() - 1));
            return LocalDateTime.now().minusMonths(months);
        } else if (retentionPeriod.endsWith("D")) {
            int days = Integer.parseInt(retentionPeriod.substring(0, retentionPeriod.length() - 1));
            return LocalDateTime.now().minusDays(days);
        } else {
            throw new IllegalArgumentException("Unsupported retention period format: " + retentionPeriod);
        }
    }
}