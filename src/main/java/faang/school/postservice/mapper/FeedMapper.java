package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.FeedResponseDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.dto.user.UserRedisDto;
import org.springframework.stereotype.Component;

@Component
public class FeedMapper {
    public FeedResponseDto toFeedResponseDto(PostRedisDto post, UserRedisDto user) {
        return new FeedResponseDto(
            user.username(),
            post.getContent(),
            post.getPublishedAt()
        );
    }
}
