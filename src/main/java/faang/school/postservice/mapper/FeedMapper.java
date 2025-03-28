package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.PostFeedReadDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedMapper {

    PostFeedReadDto toPostFeedDto(PostCache postCache);

    PostFeedReadDto toPostFeedDto(Post post);
}
