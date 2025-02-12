package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(CreatePostDto savePostDto);

    @Mapping(source = "publishedAt", target = "publishedDate")
    ReadPostDto toDto(Post post);

    List<ReadPostDto> toDtoList(List<Post> posts);
}