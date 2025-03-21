package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CommentMapper {
    @Mapping(target = "countOfLikes", expression = "java(null != comment.getLikes() ? comment.getLikes().size() : 0)" )
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "likes", ignore = true)
    Comment toEntity(CommentDto commentDto);


}
