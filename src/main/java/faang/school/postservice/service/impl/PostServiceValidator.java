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

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    UserServiceClient userServiceClient;
    ProjectServiceClient projectServiceClient;

    void validatePostDto(PostRequestDto postRequestDto) {
        Long authorId = postRequestDto.authorId();
        Long projectId = postRequestDto.projectId();

        checkAuthorship(authorId, projectId);
        if (authorId != null) {
            checkAuthorExists(authorId);
        }
        if (projectId != null) {
            checkProjectExists(projectId);
        }
    }

    void validatePostExists(Long postId, Post post) {
        if (post.getId() == null) {
            log.error("Unable to find post with id = {}", postId);
            throw new IllegalArgumentException("Unable to find post with id = " + postId);
        }
    }

    void validatePostBeforePublish(Post post) {
        if (post.isPublished()) {
            log.error("The post is already published! Post Id: {}", post.getId());
            throw new IllegalArgumentException("The post is already published!");
        }
    }

    void validatePostBeforeUpdate(Post sourcePost, Post targetPost) {
        if (sourcePost.getAuthorId() != null && !sourcePost.getAuthorId().equals(targetPost.getAuthorId())) {
            log.error("Unable to change author id to post id {}", sourcePost.getId());
            throw new IllegalArgumentException("Unable to change author id to post!");
        }
        if (sourcePost.getProjectId() != null && !sourcePost.getProjectId().equals(targetPost.getProjectId())) {
            log.error("Unable to change project id to post id {}", sourcePost.getId());
            throw new IllegalArgumentException("Unable to change project id to post!");
        }
        if (sourcePost.getAuthorId() == null && sourcePost.getProjectId() == null) {
            log.error("Unable to clear authorship of the post with id {}", sourcePost.getId());
            throw new IllegalArgumentException("Unable to clear authorship of the post!");
        }
    }

    private void checkAuthorship(Long authorId, Long projectId) {
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
