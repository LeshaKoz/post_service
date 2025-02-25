package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "size", expression = "java(getSize(resource.getSize()))")
    ResourceDto toResourceDto(Resource resource);

    List<ResourceDto> toResourceDtoList(List<Resource> resourceList);

    default String getSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        final String[] units = {"bit", "Kbit", "Mbit"};
        double convertedSize = size;
        int unitIndex = 0;

        while (convertedSize >= 1024 && unitIndex < units.length - 1) {
            convertedSize /= 1024.0;
            unitIndex++;
        }

        return String.format("%.2f %s", convertedSize, units[unitIndex]);
    }
}