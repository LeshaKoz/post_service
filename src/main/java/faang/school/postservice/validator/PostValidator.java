package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePostDraftInfo(Post post) {
        if (post.getContent().isBlank()) {
            throw new DataValidationException("Cannot create post with blank title");
        }
        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("To create post either project or user should be author");
        }
        if (post.getAuthorId() != null && post.getProjectId() != null) {
            throw new DataValidationException("Only one project or user can be author");
        }
    }

    public void validatePostAuthorExist(Post post) {
        Object author;
        if (post.getAuthorId() != null) {
            author = userServiceClient.getUser(post.getAuthorId());
        } else {
            author = projectServiceClient.getProject(post.getProjectId());
        }
        if (author == null) {
            throw new DataValidationException("Author not exists");
        }
    }

    public void validateNotPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post already published");
        }
    }

    public void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("Post already deleted");
        }
    }
}
