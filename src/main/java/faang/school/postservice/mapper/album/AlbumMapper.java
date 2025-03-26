package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    Album toEntity(AlbumCreateUpdateDto createUpdateDto);

    @Mapping(source = "posts", target = "postIds")
    AlbumDto toDtoList(Album album);

    List<AlbumDto> toDtoList(List<Album> albums);

    void update(AlbumCreateUpdateDto createUpdateDto, @MappingTarget Album album);

    default Long postToPostId(Post post) {
        return post.getId();
    }
}
