package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(CreatePostDto savePostDto);

    @Mapping(source = "publishedAt", target = "publishedDate")
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "likesToCount")
    ReadPostDto toDto(Post post);

    List<ReadPostDto> toDtoList(List<Post> posts);

    @Named("likesToCount")
    default Long likesToCount(List<Like> likes) {
        return (likes != null) ? (long) likes.size() : 0L;
    }
}