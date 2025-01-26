package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post", ignore = true)
    Comment toCommentEntity(CommentRequestDto commentRequestDto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikes")
    @Mapping(source = "post.id", target = "postId")
    CommentResponseDto toCommentResponseDto(Comment comment);

    @Named("mapLikes")
    default List<Long> mapLikes(List<Like> likes) {
        if (likes != null) {
            return likes.stream()
                    .map(Like::getId)
                    .toList();
        }
        return new ArrayList<>();
    }
}
