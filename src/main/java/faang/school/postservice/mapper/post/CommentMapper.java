package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto dto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);
}
