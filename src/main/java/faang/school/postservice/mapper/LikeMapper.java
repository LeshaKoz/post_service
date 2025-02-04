package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    LikePostDtoResponse toLikePostDtoResponse(Like like);

    @Mapping(source = "comment.id", target = "commentId")
    LikeCommentDtoResponse toLikeCommentDtoResponse(Like like);

    @Mapping(target = "post", ignore = true)
    Like toLike(LikePostDto likePostDto);

    @Mapping(target = "comment", ignore = true)
    Like toLike(LikeCommentDto likeCommentDto);
}
