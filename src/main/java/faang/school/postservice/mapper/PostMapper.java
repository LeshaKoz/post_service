package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(target = "likesId", expression = "java(mapLikeToIds(post.getLikes()))")
    @Mapping(target = "commentsId", expression = "java(mapCommentToIds(post.getComments()))")
    @Mapping(target = "albumsId", expression = "java(mapAlbumToIds(post.getAlbums()))")
    @Mapping(source = "ad.id", target = "adId")
    @Mapping(target = "resourcesId", expression = "java(mapResourceToIds(post.getResources()))")
    PostDto toDto(Post post);

    default List<Long> mapLikeToIds(List<Like> likes) {
        return likes != null ? likes.stream()
                .map(Like::getId)
                .toList() : Collections.emptyList();
    }

    default List<Long> mapCommentToIds(List<Comment> comments) {
        return comments != null ? comments.stream()
                .map(Comment::getId)
                .toList() : Collections.emptyList();
    }

    default List<Long> mapAlbumToIds(List<Album> albums) {
        return albums != null ? albums.stream()
                .map(Album::getId)
                .toList() : Collections.emptyList();
    }

    default List<Long> mapResourceToIds(List<Resource> resources) {
        return resources != null ? resources.stream()
                .map(Resource::getId)
                .toList() : Collections.emptyList();
    }
}
