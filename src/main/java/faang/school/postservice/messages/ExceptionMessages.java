package faang.school.postservice.messages;

import org.springframework.stereotype.Component;

@Component
public class ExceptionMessages {
    public final String postNotFoundException = "Post not found";
    public final String resourceNotFoundException = "Resource not found";

    public final String resourceMaxLimitException = "The resource limit for the post has been reached: 10";

    public final String fileEmptyException = "File can't be empty";
    public final String fileSizeException = "File size can't be more than 5mb";
    public final String notImageFileException = "You can send only images";

    public static final String MINIO_UPLOADING_FILE_EXCEPTION = "Error when uploading a file";
    public static final String MINIO_REMOVING_FILE_EXCEPTION = "Error when removing a file";
    public static final String MINIO_FILE_NOT_FOUND_EXCEPTION = "Error when removing a file";
}
