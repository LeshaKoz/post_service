package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentCreateDto dto);

    CommentCreateDto toDto(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToIds",
            conditionExpression = "java(comment.getLikes() != null)")
    CommentReadDto toReadDto(Comment comment);

    Comment update(@MappingTarget Comment comment, CommentUpdateDto dto);

    @Named("mapToIds")
    default List<Long> mapToIds(@Nullable List<Like> likes) {
        return likes.stream().map(Like::getId).toList();
    }
}
