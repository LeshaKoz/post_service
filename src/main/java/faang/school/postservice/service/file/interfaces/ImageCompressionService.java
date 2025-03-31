package faang.school.postservice.service.file.interfaces;

import java.io.IOException;

public interface ImageCompressionService {
    byte[] compressImage(byte[] data, String extension) throws IOException;
}
