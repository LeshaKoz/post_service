package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.LikeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    Like toLike(PostLikeDto postLikeDto);

    Like toLike(CommentLikeDto commentLikeDto);

    @Mapping(target = "authorId", source = "like.post.authorId")
    @Mapping(target = "postTitle", expression = "java(getPostBeginning(like))")
    @Mapping(target = "postId", source = "like.post.id")
    LikeEvent toLikeEvent(Like like);

    default String getPostBeginning(Like like) {
        String postContent = like.getPost().getContent();
        return postContent.substring(
                0,
                Math.min(15, postContent.length())
        );
    }
}