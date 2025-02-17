package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    AlbumResponseDto toDto(Album album);
}
