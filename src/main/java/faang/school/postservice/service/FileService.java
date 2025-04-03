package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String FILE_KEY_PATTERN= "USER_ID_%s/%s-%s";
    private static final String DEFAULT_USER_ID = "DEFAULT";
    private static final String SMALL_IMAGE_KEY_PATTERN = "small/%s";

    private static final List<String> IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".wbmp");
    private static final int MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX = 170;
    private static final int MAX_SIDE_SIZE_OF_IMAGE_PX = 1080;

    private final S3Client s3Client;
    private final UserContext userContext;


    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    @SneakyThrows
    public String uploadFile(MultipartFile file)  {
        String fileKey;

        if (isFileAnImage(file)) {
            fileKey = processingImageFile(file);
        } else {
            fileKey = createFileKey(file.getOriginalFilename());
            putObjectInS3Client(file, fileKey);
        }

        return fileKey;
    }

    public InputStream downloadFile(String fileKey) {
        return s3Client.getObject(
                buildGetObjectResponse(fileKey));
    }

    @SneakyThrows
    private String processingImageFile(MultipartFile file) {
        String fileKey = createFileKey(file.getOriginalFilename() + ".jpg");

        InputStream inputStream = file.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        putObjectInS3Client(
                makeThumbnailMultipartFile(bufferedImage, fileKey, MAX_SIDE_SIZE_OF_IMAGE_PX)
                , fileKey);

        putObjectInS3Client(
                makeThumbnailMultipartFile(bufferedImage, fileKey, MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX)
                , String.format(SMALL_IMAGE_KEY_PATTERN, fileKey));

        return fileKey;
    }


    @SneakyThrows
    private MultipartFile makeThumbnailMultipartFile(BufferedImage bufferedImage, String fileName, int maxSize) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int[] newWidthHeight = calculateNewWidthHeight(bufferedImage.getWidth(), bufferedImage.getHeight(), maxSize);

        Thumbnails.of(bufferedImage)
                .size(newWidthHeight[0], newWidthHeight[1])
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        String contentType = "image/jpeg";
        String name = "resized image";
        return new MyMultipartFile(outputStream.toByteArray(), name, fileName, contentType);
    }

    private int[] calculateNewWidthHeight(int width, int height, int maxSize) {
        if (width <= maxSize || height <= maxSize) {
            return new int[]{width, height};
        }

        int newWidth;
        int newHeight;
        if (width >= height) {
            newWidth = maxSize;
            newHeight = (int) Math.ceil(((double) newWidth * height) / width);
        } else {
            newHeight = maxSize;
            newWidth = (int) Math.ceil(((double) newHeight * width) / height);
        }

        return new int[]{newWidth, newHeight};
    }

    private boolean isFileAnImage(MultipartFile file) {
        String lowerCaseName = file.getOriginalFilename().toLowerCase();
        for (String extension : IMAGE_EXTENSIONS) {
            if (lowerCaseName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }


    @SneakyThrows
    private void putObjectInS3Client(MultipartFile file, String fileKey) {
        s3Client.putObject(
                buildPutObjectRequest(file, fileKey)
                , buildRequestBody(file));
    }

    private PutObjectRequest buildPutObjectRequest(MultipartFile file, String fileKey) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();
    }

    private RequestBody buildRequestBody(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    private GetObjectRequest buildGetObjectResponse(String fileKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
    }

    private String createFileKey(String fileName) {
        fileName = fileName.replaceAll("[^\\p{L}\\p{N}._\\-\\— ]", "_");
        String pathName = DEFAULT_USER_ID;
        String timeStamp = String.valueOf(System.nanoTime());

        return String.format(FILE_KEY_PATTERN, pathName, timeStamp, fileName);
    }
}
