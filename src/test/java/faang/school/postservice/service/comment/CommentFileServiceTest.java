package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentFileReadDto;
import faang.school.postservice.exception.FileSizeValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentFileServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentFileService commentFileService;

    @Test
    void testUploadLargeImage() throws Exception {
        long commentId = 1L;

        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/image/imageTest.jpg"));
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", imageBytes);

        Comment comment = new Comment();
        Resource resource = new Resource();
        resource.setKey("large_image_key");
        CommentFileReadDto dto = new CommentFileReadDto();

        when(commentService.getComment(commentId)).thenReturn(comment);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(resource);
        when(commentMapper.toFileDto(comment)).thenReturn(dto);

        CommentFileReadDto result = commentFileService.uploadLargeImage(commentId, file);

        assertEquals(dto, result);
        verify(commentRepository, times(1)).save(comment);
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    void testUploadSmallImage() throws Exception {
        long commentId = 1L;
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/image/imageTest.jpg"));
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", imageBytes);
        Comment comment = new Comment();
        Resource resource = new Resource();
        resource.setKey("small_image_key");
        CommentFileReadDto dto = new CommentFileReadDto();

        when(commentService.getComment(commentId)).thenReturn(comment);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(resource);
        when(commentMapper.toFileDto(comment)).thenReturn(dto);

        CommentFileReadDto result = commentFileService.uploadSmallImage(commentId, file);

        assertEquals(dto, result);
        verify(commentRepository, times(1)).save(comment);
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    void testRemoveImage() {
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setLargeImageFileKey("large_image_key");
        comment.setSmallImageFileKey("small_image_key");

        when(commentService.getComment(commentId)).thenReturn(comment);

        commentFileService.removeImage(commentId);

        verify(s3Service, times(1)).deletedFile("large_image_key");
        verify(s3Service, times(1)).deletedFile("small_image_key");
        verify(commentService, times(1)).updateComment(comment);
        assertNull(comment.getLargeImageFileKey());
        assertNull(comment.getSmallImageFileKey());
    }

    @Test
    void testVerifyImageSize() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[6 * 1024 * 1024]);

        assertThrows(FileSizeValidationException.class, () -> {
            commentFileService.uploadLargeImage(1L, file);
        });
    }
}

