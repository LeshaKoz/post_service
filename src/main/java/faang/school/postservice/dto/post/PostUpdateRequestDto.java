package faang.school.postservice.dto.post;

import lombok.Builder;

@Builder
public record PostUpdateRequestDto(String content) {
}
