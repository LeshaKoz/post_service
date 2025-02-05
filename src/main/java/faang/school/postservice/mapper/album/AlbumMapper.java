package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    Album toEntity(AlbumCreateDto dto);

    AlbumCreateDto toDto(Album entity);

    AlbumReadDto toReadDto(Album entity);

    Album update(@MappingTarget Album entity, AlbumEditDto dto);
}
