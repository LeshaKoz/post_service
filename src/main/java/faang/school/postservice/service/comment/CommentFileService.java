package faang.school.postservice.service.comment;

import com.amazonaws.services.s3.AmazonS3Client;
import faang.school.postservice.dto.comment.CommentFileReadDto;
import faang.school.postservice.exception.FileSizeValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.FileMultiPartFile;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CommentFileService {
    private static final int MAX_IMAGE_SIZE_MB = 5;
    private static final int MB_TO_BYTES = 1048576;
    private static final int MAX_LARGE_SIDE = 1080;
    private static final int MAX_SMALL_SIDE = 170;
    private final S3Service s3Service;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final AmazonS3Client s3Client;

    public CommentFileReadDto uploadLargeImage(long commentId, MultipartFile file) {
        verifyImageSize(file);
        String folder = commentId + "large_comment_attachment";

        MultipartFile resizeFile = resizeImage(file, MAX_LARGE_SIDE);
        Resource resource = s3Service.uploadFile(resizeFile, folder);

        Comment comment = commentService.getComment(commentId);
        comment.setLargeImageFileKey(resource.getKey());
        commentRepository.save(comment);

        return commentMapper.toFileDto(comment);
    }

    public CommentFileReadDto uploadSmallImage(long commentId, MultipartFile file) {
        verifyImageSize(file);
        String folder = commentId + "small_comment_attachment";

        MultipartFile resizedFile = resizeImage(file, MAX_SMALL_SIDE);
        Resource resource = s3Service.uploadFile(resizedFile, folder);

        Comment comment = commentService.getComment(commentId);
        comment.setSmallImageFileKey(resource.getKey());
        commentRepository.save(comment);

        return commentMapper.toFileDto(comment);
    }

    public void removeImage(long commentId) {
        Comment comment = commentService.getComment(commentId);
        String largeImageFileKey = comment.getLargeImageFileKey();
        String smallImageFileKey = comment.getSmallImageFileKey();
        s3Service.deletedFile(largeImageFileKey);
        s3Service.deletedFile(smallImageFileKey);
        comment.setLargeImageFileKey(null);
        comment.setSmallImageFileKey(null);
        commentService.updateComment(comment);
    }

    private void verifyImageSize(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_SIZE_MB * MB_TO_BYTES) {
            throw new FileSizeValidationException("the size must not exceed" + MAX_IMAGE_SIZE_MB + " Mb .");
        }
    }

    private MultipartFile resizeImage(MultipartFile file, int maxSize) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int newWidth;
            int newHeight;

            if (originalHeight > originalWidth) {
                newHeight = maxSize;
                newWidth = (int) Math.round(originalWidth * ((double) maxSize / originalHeight));
            } else {
                newWidth = maxSize;
                newHeight = (int) Math.round(originalHeight * ((double) maxSize / originalWidth));
            }

            BufferedImage resizedImage = new BufferedImage(
                    newWidth,
                    newHeight,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
            graphics2D.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            return new FileMultiPartFile(file.getName(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    imageBytes
            ) {
            };

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
