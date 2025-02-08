package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostUpdateDto postUpdateDto);

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "likesToDto")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "commentsToDto")
    PostUpdateDto toDto(Post post);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "likesToDto")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "commentsToDto")
    PostDto toPostDto(Post post);

    @Named("likesToDto")
    default List<Long> likesToDto (List<Like> likes) {
        if(likes == null) {
            return List.of();
        }
        return likes.stream().map(Like::getId).toList();
    }

    @Named("commentsToDto")
    default List<Long> commentsToDto (List<Comment> comments) {
        if(comments == null) {
            return List.of();
        }
        return comments.stream().map(Comment::getId).toList();
    }
}
