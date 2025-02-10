package faang.school.postservice.dto.hashtag;

import java.util.List;

public record HashtagReadDto(
        long id,
        String name,
        List<Long> postIds
) {
}