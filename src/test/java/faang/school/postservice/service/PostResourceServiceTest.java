package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.S3Properties;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostResourceServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private PostResourceService postResourceService;

    private static final long POST_ID = 1L;
    private static final String IMAGE_KEY = "imageKey123";
    private static final String IMAGE_NAME = "image.jpg";
    private static final int MAX_WIDTH_HORIZONTAL = 1080;
    private static final int MAX_HEIGHT_HORIZONTAL = 566;
    private static final int MAX_SIDE_SQUARE = 1080;
    private static final long MAX_IMAGE_SIZE_MB = 5L;
    private static final int MAX_IMAGE_COUNT_FOR_POST = 10;

    private Post post;
    private S3Properties.ImageProperties imageProperties;

    @BeforeEach
    void setUp() {

        imageProperties = new S3Properties.ImageProperties();
        imageProperties.setMaxWidthHorizontal(MAX_WIDTH_HORIZONTAL);
        imageProperties.setMaxHeightHorizontal(MAX_HEIGHT_HORIZONTAL);
        imageProperties.setMaxSideSquare(MAX_SIDE_SQUARE);

        post = Post.builder()
                .id(POST_ID)
                .deleted(false)
                .fileKeys(new ArrayList<>(List.of("key123")))
                .build();
    }

    @Test
    void testAddPostImage_Successfully() throws IOException {

        byte[] imageContent = Files.readAllBytes(Paths.get("src/test/java/faang/school/postservice/resources/test-image.jpg"));

        MockMultipartFile image = new MockMultipartFile("file", IMAGE_NAME, "image/jpeg", imageContent);

        when(s3Properties.getMaxImagesCountForPost()).thenReturn(MAX_IMAGE_COUNT_FOR_POST);
        when(s3Properties.getMaxImageSizeMb()).thenReturn(MAX_IMAGE_SIZE_MB);
        when(s3Properties.getImage()).thenReturn(imageProperties);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(IMAGE_KEY);

        ResourceDto result = postResourceService.addPostImage(POST_ID, image);

        assertNotNull(result);
        assertEquals(IMAGE_KEY, result.key());
        assertEquals(IMAGE_NAME, result.name());
        verify(postRepository).save(post);
    }

    @Test
    void testDeleteImageByKey_Success() {

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        String resultKey = postResourceService.deleteImageByKey(POST_ID, "key123");

        assertEquals("key123", resultKey);
        verify(postRepository).save(post);
        verify(s3Service).deleteFile("key123");
    }

    @Test
    void testDeleteImageByKey_NotFound() {

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                postResourceService.deleteImageByKey(POST_ID, "nonexistent")
        );
        assertTrue(exception.getMessage().contains("не найдено"));
    }

    @Test
    void testGetImageByKey_Success() throws Exception {
        byte[] imageData = "image bytes".getBytes();
        InputStream inputStream = new ByteArrayInputStream(imageData);
        when(s3Service.downloadFile("key123")).thenReturn(inputStream);

        byte[] result = postResourceService.getImageByKey("key123");

        assertArrayEquals(imageData, result);
        verify(s3Service).downloadFile("key123");
    }

    @Test
    void testGetAllImageKeys_ByPostId() {

        post.setFileKeys(new ArrayList<>(Arrays.asList("key1", "key2", "key3")));

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        List<String> result = postResourceService.getAllImageKeysByPostId(1L);

        assertEquals(Arrays.asList("key1", "key2", "key3"), result);
    }

    @Test
    void testDeleteAllImages_ByPostId() {

        post.setFileKeys(new ArrayList<>(Arrays.asList("key1", "key2")));

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        postResourceService.deleteAllImagesByPostId(POST_ID);

        assertTrue(post.getFileKeys().isEmpty());
        verify(postRepository).save(post);
        verify(s3Service).deleteFile("key1");
        verify(s3Service).deleteFile("key2");
    }

    @Test
    void addPostImage_InvalidFileType() {
        MultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "text".getBytes());
        assertThrows(EntityNotFoundException.class, () -> postResourceService.addPostImage(POST_ID, textFile));
    }
}