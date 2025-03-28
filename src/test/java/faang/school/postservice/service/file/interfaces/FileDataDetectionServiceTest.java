package faang.school.postservice.service.file.interfaces;

import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.service.file.implementations.FileDataDetectionServiceImpl;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDataDetectionServiceTest {
    @Mock
    private Tika tika;

    @InjectMocks
    private FileDataDetectionServiceImpl fileDataDetectionService;

    @Test
    void testDetectFileTypeAndExtension() throws IOException {
        String detectedType = "image";
        String detectedExtension = "jpeg";
        String typeWithExtension = "%s/%s".formatted(detectedType, detectedExtension);
        byte[] fileBytes = new byte[]{66, 6, 9, 99};
        MultipartFile file = mock(MultipartFile.class);
        when(tika.detect(file.getInputStream())).thenReturn(typeWithExtension);
        when(file.getBytes()).thenReturn(fileBytes);

        FileMetaData fileMetaData = fileDataDetectionService.detect(file);

        assertNotNull(fileMetaData);
        assertEquals(fileBytes, fileMetaData.getData());
        assertEquals(file.getOriginalFilename(), fileMetaData.getOriginalName());
        assertEquals(detectedType, fileMetaData.getType());
        assertEquals(detectedExtension, fileMetaData.getExtension());
    }

    @Test
    void testDetectFile_withUnknownType() throws IOException {
        String detectedType = "unknown";
        byte[] fileBytes = new byte[]{66, 6, 9, 99};
        MultipartFile file = mock(MultipartFile.class);
        when(tika.detect(file.getInputStream())).thenReturn(detectedType);
        when(file.getBytes()).thenReturn(fileBytes);

        FileMetaData fileMetaData = fileDataDetectionService.detect(file);

        assertNotNull(fileMetaData);
        assertEquals(fileBytes, fileMetaData.getData());
        assertEquals(file.getOriginalFilename(), fileMetaData.getOriginalName());
        assertEquals("another", fileMetaData.getType());
        assertEquals("", fileMetaData.getExtension());
    }

    @Test
    void testDetectFile_withIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(tika.detect(file.getInputStream())).thenThrow(new IOException());

        assertThrows(IOException.class, () -> fileDataDetectionService.detect(file));
    }
}
