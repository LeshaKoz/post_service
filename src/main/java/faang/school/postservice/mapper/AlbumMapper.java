package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.CreateAlbumRequestDto;
import faang.school.postservice.dto.album.UpdateAlbumRequestDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(target = "postIds", expression = "java(mapPostIds(album.getPosts()))")
    AlbumDto toDto(Album album);

    List<AlbumDto> toDtoList(List<Album> albums);

    @Mapping(target = "posts", ignore = true)
    Album toEntity(AlbumDto albumDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    Album toEntity(CreateAlbumRequestDto createAlbumRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    void updateAlbumFromDto(UpdateAlbumRequestDto request, @MappingTarget Album album);

    default List<Long> mapPostIds(List<Post> posts) {
        return posts == null ? Collections.emptyList() : posts.stream().map(Post::getId).collect(Collectors.toList());
    }
}
