package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.utils.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    Album toEntity(AlbumCreateDto dto);

    AlbumReadDto toDto(Album entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "description", conditionQualifiedByName = "isNotBlank")
    void updateEntityFromDto(AlbumUpdateDto dto, @MappingTarget Album entity);

    @Condition
    @Named("isNotBlank")
    default boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }
}
