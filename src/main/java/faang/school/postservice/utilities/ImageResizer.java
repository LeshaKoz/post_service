package faang.school.postservice.utilities;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageResizer {

    public MultipartFile resizeImage(MultipartFile image, int targetWidth, int targetHeight) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Недопустимый формат изображения");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(bufferedImage)
                .size(targetWidth, targetHeight)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        byte[] resizedImageBytes = outputStream.toByteArray();

        return new MultipartFile() {

            @Override
            public String getName() {
                return image.getName();
            }

            @Override
            public String getOriginalFilename() {
                return image.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return image.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return resizedImageBytes.length == 0;
            }

            @Override
            public long getSize() {
                return resizedImageBytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return resizedImageBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(resizedImageBytes);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(resizedImageBytes);
                }
            }
        };
    }
}