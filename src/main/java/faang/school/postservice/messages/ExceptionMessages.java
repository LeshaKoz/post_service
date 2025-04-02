package faang.school.postservice.messages;

import org.springframework.stereotype.Component;

@Component
public class ExceptionMessages {
    public static final String POST_NOT_FOUND_EXCEPTION = "Post not found";
    public static final String RESOURCE_NOT_FOUND_EXCEPTION = "Resource not found";
    public static final String RESOURCE_POST_ID_NOT_EQUALS_POST_ID_EXCEPTION = "Resource post id not equals post id";

    public static final String RESOURCE_MAX_LIMIT_EXCEPTION = "The resource limit for the post has been reached: 10";

    public static final String FILE_SIZE_EXCEPTION = "File size can't be more than 5mb";
    public static final String FILE_ORIGINAL_NAME_EMPTY_EXCEPTION = "File original name can't be null";
    public static final String FILE_NAME_EMPTY_EXCEPTION = "File original name can't be null";

    public static final String MINIO_UPLOADING_FILE_EXCEPTION = "Error when uploading a file";
    public static final String MINIO_REMOVING_FILE_EXCEPTION = "Error when removing a file";
    public static final String MINIO_FILE_NOT_FOUND_EXCEPTION = "Error when removing a file";
    public static final String MINIO_BUCKET_NOT_FOUND_EXCEPTION = "Bucket not found";
}
