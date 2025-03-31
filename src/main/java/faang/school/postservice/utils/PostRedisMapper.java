package faang.school.postservice.utils;

import faang.school.postservice.dto.posts.PostRedis;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostRedisMapper {
    Post toEntity(PostRedis postRedis);

    PostRedis toDto(Post post);
}
