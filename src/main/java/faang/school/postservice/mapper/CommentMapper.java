package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования CommentCreateDto и Comment в CommentViewDto.
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Преобразует CommentCreateDto в сущность Comment.
     *
     * @param commentCreateDto DTO для создания комментария.
     * @return Сущность Comment.
     */
    Comment toEntity(CommentCreateDto commentCreateDto);

    /**
     * Преобразует сущность Comment в CommentViewDto.
     *
     * @param comment Сущность Comment.
     * @return CommentViewDto.
     */
    @Mapping(source = "post.id", target = "postId")
    CommentViewDto toViewDto(Comment comment);
}
