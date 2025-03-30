package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDtoRs;
import faang.school.postservice.dto.resource.ResourceDtoRq;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.service.image.S3Service;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ImageService imageService;

    @Value("${app.posts.files.image-max-width}")
    private int imageMaxWidth;
    @Value("${app.posts.files.image-max-height}")
    private int imageMaxHeight;

    public List<ResourceDtoRs> save(Post post, MultipartFile[] files) {
        List<ResourceDtoRs> savedResources = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            try {
                BufferedImage image = ImageIO.read(files[i].getInputStream());
                int width = image.getWidth();
                int height = image.getHeight();
                String contentType = files[i].getContentType();

                String folder = "post" + post.getId();
                String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), files[i].getOriginalFilename());

                ByteArrayOutputStream byteArrayOutputStream;
                if (width > imageMaxWidth || height > imageMaxHeight) {
                    byteArrayOutputStream = imageService.resizeImage(files[i], imageMaxWidth);
                } else {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    Thumbnails.of(files[i].getInputStream())
                            .toOutputStream(byteArrayOutputStream);
                }
                ResourceDtoRq requestDto = createResourceRequest(files[i], key, post);
                savedResources.add(saveFile(requestDto, byteArrayOutputStream, contentType, key));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return savedResources;
    }

    private ResourceDtoRq createResourceRequest(MultipartFile file, String key, Post post) {
        return ResourceDtoRq.builder()
                .name(file.getName())
                .type(file.getContentType())
                .key(key)
                .postId(post.getId())
                .build();
    }

    @Transactional
    protected ResourceDtoRs saveFile(ResourceDtoRq requestDto, ByteArrayOutputStream byteArrayOutputStream, String contentType, String key) {
        Resource savedResource = resourceRepository.save(resourceMapper.toResourceEntity(requestDto));
        s3Service.uploadFile(byteArrayOutputStream.size(), contentType, key, byteArrayOutputStream.toByteArray());
        return resourceMapper.toResourceDtoResponse(savedResource);
    }
    
    public InputStream downloadResource(Long resourceId){
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException("Resource with id [%d] not found in database".formatted(resourceId)));
    }
}
