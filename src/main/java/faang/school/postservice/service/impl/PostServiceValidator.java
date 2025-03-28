package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.project.ProjectResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserDtoAdapter userDtoAdapter;

    void validatePostDto(PostCreateRequestDto postCreateRequestDto) {
        Long authorId = postCreateRequestDto.authorId();
        Long projectId = postCreateRequestDto.projectId();

        if (authorId != null) {
            //TODO сломалось, надо вернуть в работу потом
            //    checkAuthorExists(authorId);
        }
        if (projectId != null) {
            checkProjectExists(projectId);
        }
        if (authorId != null && projectId != null) {
            checkAuthorship(authorId, projectId);
        }
    }

    void validatePostBeforePublish(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("The post is already published! Post id = " + post.getId());
        }
    }

    void validatePostBeforeUpdate(Post sourcePost, Post targetPost) {
        if (sourcePost == null) {
            throw new IllegalArgumentException("Post to update is null!");
        }
        Long postId = sourcePost.getId();
        Long authorId = sourcePost.getAuthorId();
        Long projectId = sourcePost.getProjectId();

        if (!authorId.equals(targetPost.getAuthorId())) {
            throw new IllegalArgumentException("Unable to change author id to post! Post id = "
                    + postId + ", Author Id = " + authorId);
        }
        if (projectId != null && !projectId.equals(targetPost.getProjectId())) {
            throw new IllegalArgumentException("Unable to change project id to post! Post id = "
                    + postId + ", Project Id = " + projectId);
        }
    }

    private void checkAuthorship(Long authorId, Long projectId) {
        if ((null == authorId || authorId <= 0) && (null == projectId || projectId <= 0)) {
            throw new IllegalArgumentException("Either the author or the project of the post must be provided");
        }
    }

    private void checkAuthorExists(Long authorId) {
        UserResponseDto userResponseDto = userServiceClient.getUser(authorId);
        UserDto userDto = userDtoAdapter.toUserDto(userResponseDto);
        if (!authorId.equals(userDto.id())) {
            throw new IllegalArgumentException("Unable to find user with id = " + authorId);
        }
    }

    private void checkProjectExists(Long projectId) {
        ProjectResponseDto projectDto = projectServiceClient.getProject(projectId);
        if (!projectId.equals(projectDto.id())) {
            throw new IllegalArgumentException("Unable to find project with id = " + projectId);
        }
    }
}
