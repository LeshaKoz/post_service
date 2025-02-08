package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostRedisMapper {

    PostRedis toPostCache(Post postDto);

    Post toDto(PostRedis postRedis);
}
