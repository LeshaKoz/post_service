package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    Album toEntity(AlbumCreateDto dto);

    AlbumCreateDto toDto(Album entity);

    @Mapping(source = "posts", target = "postIds",
            qualifiedByName = "mapToLong",
            conditionExpression = "java(entity.getPosts() != null)")
    AlbumReadDto toReadDto(Album entity);

    void update(@MappingTarget Album entity, AlbumEditDto dto);

    @Named("mapToLong")
    default List<Long> mapToLong(List<Post> posts) {
        return posts.stream().map(Post::getId).toList();
    }
}
