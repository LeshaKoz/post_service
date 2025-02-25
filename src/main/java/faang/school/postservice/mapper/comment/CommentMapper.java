package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.cache.CommentCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comment);

    CommentEvent toCommentEvent(CommentDto commentDto);

    CommentCache toCommentCache(CommentEvent commentEvent);
}
