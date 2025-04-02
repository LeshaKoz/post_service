package faang.school.postservice.util;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.ResourcePostIdNotEqualsPostIdException;
import faang.school.postservice.exception.not_found_exceptions.PostNotFoundException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import faang.school.postservice.mapper.ResourceMapperImpl;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.minio.MinioConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostResourceRepository;
import faang.school.postservice.service.PostResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostResourceServiceTest {
    private final Long postId = 1L;
    private final Long resourceId = 1L;
    private static final int STANDARD_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;
    private static final int VERTICAL_SQUARE_HEIGHT = 1080;
    private static final long BYTES_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Integer MAX_COUNT_OF_RESOURCES = 10;
    private MultipartFile file;
    private Post post;
    private Resource resource;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostResourceRepository postResourceRepository;
    @Spy
    private ResourceMapperImpl resourceMapper = new ResourceMapperImpl();
    @Mock
    private MinioConfig minioConfig;
    @InjectMocks
    private PostResourceService postResourceService;

    @Captor
    ArgumentCaptor<Post> postArgumentCaptor;
    @Captor
    ArgumentCaptor<Resource> resourceArgumentCaptor;
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;


    @BeforeEach
    void setUp() {
        file = createMultipartFile("file", "puppy,jpg", "image/jpeg", 123);
        post = Post.builder().id(1L).build();
        post.getResources().add(new Resource());
        resource = Resource.builder().id(1L).post(post).build();
    }

    @Test
    void testValidateWithEmptyFileName() {
        MultipartFile file = createMultipartFile("  ", "test", "test", 123);
        InvalidFileException exception = assertThrows(InvalidFileException.class,
                () -> postResourceService.add(1L, List.of(file)));

        assertEquals(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION, exception.getMessage());
    }

    @Test
    void testValidateWithEmptyFileOriginalName() {
        MultipartFile file = createMultipartFile("test", "  ", "test", 123);

        InvalidFileException exception = assertThrows(InvalidFileException.class,
                () -> postResourceService.add(1L, List.of(file)));

        assertEquals(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION, exception.getMessage());
    }

    @Test
    void testValidateMaxFileSize() {
        MultipartFile file = createMultipartFile("test", "test",
                "test", BYTES_FILE_SIZE + 1);

        InvalidFileException exception = assertThrows(InvalidFileException.class,
                () -> postResourceService.add(1L, List.of(file)));

        assertEquals(ExceptionMessages.FILE_SIZE_EXCEPTION, exception.getMessage());
    }

    @Test
    void shouldThrowWhenPostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,
                () -> postResourceService.add(999L, List.of(file)));
    }

    @Test
    void shouldThrowWhenMaxResourcesReached() {
        post = new Post();
        post.setResources(new ArrayList<>(Collections.nCopies(MAX_COUNT_OF_RESOURCES, new Resource())));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        MaxResourcesReachedException exception = assertThrows(MaxResourcesReachedException.class,
                () -> postResourceService.add(1L, List.of(file)));

        assertEquals(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION, exception.getMessage());
    }

    private byte[] createTestImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void shouldProcessImageCorrectly() throws Exception {
        byte[] imageBytes = createTestImage(100, 100);
        MultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", imageBytes);

        Post mockPost = new Post();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mockPost));

        Resource mockResource = new Resource();
        when(minioConfig.uploadImage(
                any(InputStream.class),
                any(byte[].class),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(mockResource);

        ResourceDto result = postResourceService.add(1L, List.of(imageFile));

        assertNotNull(result);
        verify(minioConfig).uploadImage(
                any(InputStream.class),
                any(byte[].class),
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    void resizeImageHorizontal() throws Exception {
        BufferedImage originalImage = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB);
        BufferedImage resizedImage = getResizedImage(originalImage);
        assertEquals(STANDARD_WIDTH, resizedImage.getWidth(),
                "Width should be " + STANDARD_WIDTH);
        assertEquals(HORIZONTAL_HEIGHT, resizedImage.getHeight(),
                "Height should be" + HORIZONTAL_HEIGHT);
    }

    @Test
    void resizeImageSquare() throws Exception {
        BufferedImage originalImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        BufferedImage resizedImage = getResizedImage(originalImage);

        assertEquals(STANDARD_WIDTH, resizedImage.getWidth(),
                "Width should be " + STANDARD_WIDTH);
        assertEquals(VERTICAL_SQUARE_HEIGHT, resizedImage.getHeight(),
                "Height should be" + VERTICAL_SQUARE_HEIGHT);
    }

    @Test
    void shouldProcessVideoCorrectly() {
        MultipartFile videoFile = createMultipartFile(
                "file", "test.mp4", "video/mp4", 123
        );

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(minioConfig.uploadVideoOrAudio(any(), any()))
                .thenReturn(new Resource());

        assertDoesNotThrow(() -> postResourceService.add(1L, List.of(videoFile)));
    }

    @Test
    void shouldThrowResourceNotFoundException() {
        when(postResourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> postResourceService.delete(postId, resourceId));
    }

    @Test
    void shouldThrowPostNotFoundException() {
        when(postResourceRepository.findById(resourceId)).thenReturn(Optional.of(new Resource()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class,
                () -> postResourceService.delete(postId, resourceId));
    }

    @Test
    void shouldResourcePostIdNotEqualsPostIdException() {
        resource = Resource.builder()
                .id(1L)
                .post(Post.builder().id(2L).build())
                .build();

        when(postResourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(ResourcePostIdNotEqualsPostIdException.class,
                () -> postResourceService.delete(postId, resourceId));
    }

    @Test
    void testDeleteMethod() {
        when(postResourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            post.getResources().clear(); // Очищаем ресурсы в нашем mock-объекте
            return savedPost;
        });

        postResourceService.delete(postId, resourceId);

        assertEquals(0, post.getResources().size());
        verify(postRepository).save(postArgumentCaptor.capture());
        verify(minioConfig).delete(stringArgumentCaptor.capture());
        verify(postResourceRepository).deleteById(resourceId);
    }

    private BufferedImage getResizedImage(BufferedImage originalImage) throws Exception {
        byte[] imageBytes = toByteArray(originalImage, "jpg");
        MultipartFile file = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpeg", imageBytes);

        Method resizeMethod = PostResourceService.class
                .getDeclaredMethod("resizeImage", MultipartFile.class);
        resizeMethod.setAccessible(true);
        BufferedImage resizedImage = (BufferedImage) resizeMethod.invoke(
                postResourceService,
                file
        );

        return resizedImage;
    }

    private byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }


    private MockMultipartFile createMultipartFile(String name, String originalFileName,
                                                  String contentType, long size) {
        return new MockMultipartFile(
                name,
                originalFileName,
                contentType,
                new byte[(int) size]
        );
    }
}
