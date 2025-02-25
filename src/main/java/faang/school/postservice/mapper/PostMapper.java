package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Mapping(target = "countOfLikes", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    @Mapping(target = "comments", expression = "java(mapComments(post.getComments()))")
    PostCache toCache(Post post);

    default TreeSet<Comment> mapComments(List<Comment> comments) {
        return new TreeSet<>(Optional.ofNullable(comments).orElse(List.of())
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList());
    }
}
