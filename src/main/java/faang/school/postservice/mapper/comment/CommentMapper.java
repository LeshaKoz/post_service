package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentFileReadDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface CommentMapper {

    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto dto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment entity);

    List<CommentDto> toDtoList(List<Comment> comments);

    @Mapping(target = "commentId", source = "comment.id")
    CommentFileReadDto toFileDto(Comment comment);


}