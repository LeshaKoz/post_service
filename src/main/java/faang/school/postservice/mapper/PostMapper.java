package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.dto.post.RequestPostHashtagDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    Post toEntity(RequestPostDto requestPostDto);

    Post toEntity(RequestPostHashtagDto requestPostHashtagDto);

    ResponsePostDto toDto(Post post);

    List<ResponsePostDto> toDto(List<Post> list);
}