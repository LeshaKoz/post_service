package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PostControllerValidator {
    static void validateUpdateDto(PostUpdateRequestDto postUpdateRequestDto) {
        if (StringUtils.isBlank(postUpdateRequestDto.content())) {
            throw new IllegalArgumentException("Incorrect Post request DTO, empty content");
        }
    }

    static void validateCreateDto(PostCreateRequestDto postCreateRequestDto) {
        if (StringUtils.isBlank(postCreateRequestDto.content())) {
            throw new IllegalArgumentException("Incorrect Post request DTO, empty content");
        }
    }
}
