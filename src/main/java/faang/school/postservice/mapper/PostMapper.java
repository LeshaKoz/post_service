package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    Post toEntity(RequestPostDto requestPostDto);

    ResponsePostDto toDto(Post post);

    List<ResponsePostDto> toDto(List<Post> list);

    PostRedisDto toRedisEntity(Post post);

    List<PostRedisDto> toRedisEntityList(List<Post> list);
}