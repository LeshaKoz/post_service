package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toComment(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "authorId", target = "authorId")
    CommentDto toCommentDto(Comment comment);
}
