package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostControllerValidator {

    void validatePostId(Long postId) {
        validateEntityId("Post", postId);
    }

    void validateUserId(Long userId) {
        validateEntityId("User", userId);
    }

    void validateProjectId(Long projectId) {
        validateEntityId("Project", projectId);
    }

    private void validateEntityId(String checkedEntity, Long id) {
        if (null == id || id < 1) {
            log.error("{} id is incorrect or empty : {}", checkedEntity, id);
            throw new IllegalArgumentException(checkedEntity + " id is incorrect or empty");
        }
    }

    void validateDto(PostRequestDto postRequestDto) {
        if (null == postRequestDto.id() || postRequestDto.id() < 1) {
            log.error("Incorrect Post request DTO, empty id. DTO : {}", postRequestDto);
            throw new IllegalArgumentException("Incorrect Request DTO, empty id");
        }

        if (StringUtils.isBlank(postRequestDto.content())) {
            log.error("Incorrect Post request DTO, empty content. DTO : {}", postRequestDto);
            throw new IllegalArgumentException("Incorrect Post request DTO, empty content");
        }
    }
}
