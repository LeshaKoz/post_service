package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

        @Mapping(target = "postId", source = "post.id")
        CommentDto toDto(Comment comment);

        @Mapping(target = "post.id", source = "postId")
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "largeImageFileKey", ignore = true)
        @Mapping(target = "smallImageFileKey", ignore = true)
        Comment toEntity(CommentCreateDto createDto);

        @Mapping(target = "post", ignore = true)
        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "largeImageFileKey", ignore = true)
        @Mapping(target = "smallImageFileKey", ignore = true)
        void updateEntity(CommentUpdateDto updateDto, @MappingTarget Comment comment);
}