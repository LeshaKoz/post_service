package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 s3;

    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Value("${resource.folder}")
    private String folder;
    @Value("${resource.max-width}")
    private int maxWidth;
    @Value("${resource.max-height}")
    private int maxHeight;

    public Resource uploadFile(MultipartFile file) {
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        String key = folder + "/" + file.getOriginalFilename();
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            processAndPutImage(request);
        } catch (IOException e) {
            log.error("Failed to upload file: ", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size(fileSize)
                .build();
    }

    public void deleteFile(String key) {
        s3.deleteObject(bucketName, key);
    }

    private void processAndPutImage(PutObjectRequest request) throws IOException {
        BufferedImage image = ImageIO.read(request.getInputStream());
        int width = image.getWidth();
        int height = image.getHeight();
        int standartWidth = maxWidth;
        int standartHeight = maxHeight;
        boolean isHorizontal = width > height;
        boolean isVertical = width < height;
        boolean isSquareAndSidesBiggerMaxValue = width == height && width > standartWidth;
        if (isHorizontal) {
            if ((width > standartWidth) && (height > standartHeight)) {
                scaleAndPutImage(image, standartWidth, standartHeight, request);
            } else if (width > standartWidth) {
                scaleAndPutImage(image, standartWidth, height, request);
            } else if (height > standartHeight) {
                scaleAndPutImage(image, width, standartHeight, request);
            }
        } else if (isSquareAndSidesBiggerMaxValue) {
            scaleAndPutImage(image, standartWidth, standartWidth, request);
        } else if (isVertical) {
            if ((width > standartHeight) && (height > standartWidth)) {
                scaleAndPutImage(image, standartHeight, standartWidth, request);
            } else if (width > standartHeight) {
                scaleAndPutImage(image, standartHeight, height, request);
            } else if (height > standartWidth) {
                scaleAndPutImage(image, width, standartWidth, request);
            }
        } else {
            s3.putObject(request);
        }
    }

    private void scaleAndPutImage(BufferedImage image, int maxWidth, int maxHeight, PutObjectRequest request) throws IOException {
        BufferedImage resizedImage = createResizedCopy(image, maxWidth, maxHeight);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        request.setInputStream(inputStream);
        request.getMetadata().setContentLength(inputStream.available());
        request.getMetadata().setContentType("image/png");
        os.close();
        inputStream.close();
        s3.putObject(request);
    }

    private BufferedImage createResizedCopy(BufferedImage originalImage, int scaledWidth, int scaledHeight) {
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
