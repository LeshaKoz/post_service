package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.model.cache.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CacheMapper {
    @Mapping(target = "postId" , source = "id")
    PostCache toPostCache(PostReadDto dto);
}
