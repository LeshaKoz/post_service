package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.AlbumRequestDto;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlbumMapper {


    @Mapping(target = "postsIds", expression = "java(mapPostsToIds(album.getPosts()))")
    AlbumResponseDto toDto(Album album);

    Album toEntity(AlbumRequestDto dto);

    void update(AlbumRequestDto dto, @MappingTarget Album album);

    default List<Long> mapPostsToIds(List<Post> posts) {
        if (posts == null) {
            return List.of();
        }
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
