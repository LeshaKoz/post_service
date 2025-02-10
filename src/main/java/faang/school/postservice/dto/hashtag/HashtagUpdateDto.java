package faang.school.postservice.dto.hashtag;

import lombok.Builder;

import java.util.List;

@Builder
public record HashtagUpdateDto(
        long id,
        String name
) {
}