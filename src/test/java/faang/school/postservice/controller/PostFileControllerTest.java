package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostFileDto;
import faang.school.postservice.service.PostFileService;
import faang.school.postservice.service.file.FileData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostFileControllerTest {

    @Mock
    private PostFileService postFileService;

    @InjectMocks
    private PostFileController postFileController;

    @Test
    void testUploadFilesToPostSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "file content".getBytes());
        List<MultipartFile> files = Collections.singletonList(file);
        long postId = 1L;
        String response = postFileController.uploadFilesToPost(files, postId, 123L);
        assertEquals("Upload started", response);
        verify(postFileService, times(1)).uploadFiles(files, postId);
    }


    @Test
    void testGetPostFilesInfoSuccess() {
        long postId = 1L;
        List<PostFileDto> mockFiles = List.of(
                PostFileDto.builder().id(1L).key("key1").name("name1").type("type1").postId(postId).build(),
                PostFileDto.builder().id(2L).key("key2").name("name2").type("type2").postId(postId).build()
        );
        when(postFileService.getPostFilesInfo(postId)).thenReturn(mockFiles);
        List<PostFileDto> response = postFileController.getPostFilesInfo(postId, 123L);
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(postFileService, times(1)).getPostFilesInfo(postId);
    }

    @Test
    void testGetPostFilesInfoNotFound() {
        long postId = 1L;
        when(postFileService.getPostFilesInfo(postId)).thenReturn(Collections.emptyList());
        List<PostFileDto> response = postFileController.getPostFilesInfo(postId, 123L);
        assertTrue(response.isEmpty());
        verify(postFileService, times(1)).getPostFilesInfo(postId);
    }

    @Test
    void testDeletePostFileSuccess() {
        long postId = 1L;
        long fileId = 1L;
        postFileController.deletePostFile(postId, fileId, 123L);
        verify(postFileService, times(1)).deletePostFile(postId, fileId);
    }

    @Test
    void testDownloadPostFileSuccess() {
        long postId = 1L;
        long fileId = 1L;
        byte[] fileDataContent = "file content".getBytes();
        FileData fileData = new FileData(fileDataContent, "test.txt", "text", "txt");
        when(postFileService.downloadFile(postId, fileId)).thenReturn(fileData);
        ResponseEntity<byte[]> response = postFileController.downloadPostFile(postId, fileId, 123L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileDataContent, response.getBody());
        verify(postFileService, times(1)).downloadFile(postId, fileId);
    }
}
