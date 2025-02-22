package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentEventDto toDto(Comment entity);
}