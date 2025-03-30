package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.utils.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "hashtags", ignore = true)
    Post toEntity(PostCreateDto dto);

    @Mapping(target = "likesCount",
            expression = "java(entity.getLikes().size())")
    PostReadDto toDto(Post entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "scheduledAt", conditionQualifiedByName = "isNotNull")
    void updateEntityFromDto(PostUpdateDto dto, @MappingTarget Post entity);

    @Condition
    @Named("isNotBlank")
    default boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapHashtagsToIds(List<Hashtag> hashtags) {
        return Optional.ofNullable(hashtags)
                .orElse(Collections.emptyList())
                .stream()
                .map(Hashtag::getId)
                .toList();
    }

    @Condition
    @Named("isNotNull")
    default boolean isNotNull(LocalDateTime value) {
        return value != null;
    }

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "likes", target = "numLikes", qualifiedByName = "mapLikesToNumLikes")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapComments")
    @Mapping(target = "numViews", ignore = true)
    @Mapping(target = "version", ignore = true)
    PostCache toPostCache(Post entity);

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        return (likes == null) ? new ArrayList<>() :
                likes.stream()
                        .map(Like::getId)
                        .toList();
    }

    @Named("mapLikesToNumLikes")
    default long mapLikesToNumLikes(List<Like> likes) {
        return (likes == null) ? 0L : likes.size();
    }

    @Named("mapComments")
    default LinkedHashSet<String> mapComments(List<Comment> comments) {
        return (comments == null) ? new LinkedHashSet<>() :
                comments.stream()
                        .map(Comment::getContent)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
