package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    UserServiceClient userServiceClient;
    ProjectServiceClient projectServiceClient;

    public void validatePostDto(PostRequestDto postRequestDto) {
        Long authorId = postRequestDto.authorId();
        Long projectId = postRequestDto.projectId();

        checkAuthority(authorId, projectId);
        checkAuthorExists(authorId);
        checkProjectExists(projectId);
    }

    public void validatePostExists(Long postId, Optional<Post> optionalPost) {
        if (optionalPost.isEmpty()) {
            log.error("Unable to find post with id = {}", postId);
            throw new IllegalArgumentException("Unable to find post with id = " + postId);
        }
    }

    public void validatePostBeforePublish(Post post) {
        if (post.isPublished()) {
            log.error("The post is already published! Post Id: {}", post.getId());
            throw new IllegalArgumentException("The post is already published!");
        }
    }

    private void checkAuthority(Long authorId, Long projectId) {
        if ((null == authorId || authorId <= 0) && (null == projectId || projectId <= 0)) {
            log.error("Either the author or the project of the post must be provided. AuthorId: {}, ProjectId: {}",
                    authorId, projectId);
            throw new IllegalArgumentException("Either the author or the project of the post must be provided");
        }
    }

    private void checkAuthorExists(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (!authorId.equals(userDto.id())) {
            log.error("Unable to find user with id = {}", authorId);
            throw new IllegalArgumentException("Unable to find user with id = " + authorId);
        }
    }

    private void checkProjectExists(Long projectId) {
        ProjectDto projectDto = projectServiceClient.getProject(projectId);
        if (!projectId.equals(projectDto.id())) {
            log.error("Unable to find project with id = {}", projectId);
            throw new IllegalArgumentException("Unable to find project with id = " + projectId);
        }
    }


}
