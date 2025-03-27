package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "largeImageFileKey", ignore = true)
        @Mapping(target = "smallImageFileKey", ignore = true)
        @Mapping(target = "post.id", source = "commentDto.postId")
        Comment toEntity(CommentDto commentDto);

        @Mapping(target = "postId", source = "post.id")
        CommentDto toDto(Comment comment);
        }
