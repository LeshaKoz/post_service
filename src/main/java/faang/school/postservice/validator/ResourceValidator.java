package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class ResourceValidator {
    @Value("${resource.limit}")
    private int limit;

    public void validateResourceLimit(int count) {
        if (count >= limit) {
            throw new DataValidationException("Cannot upload more than " + limit + " images");
        }
    }

    public void validateResourceBelongsToPost(Resource resource, long postId) {
        if (resource.getPost().getId() != postId) {
            throw new DataValidationException("Resource does not belong to post");
        }
    }

    public void validateResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        List<String> availableTypes = Arrays.asList("image/jpeg", "image/jpeg", "image/jpg");
        if (!availableTypes.contains(contentType)) {
            throw new DataValidationException("Invalid file type");
        }
    }
}
