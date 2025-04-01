package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDtoRs;
import faang.school.postservice.dto.resource.ResourceDtoRq;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    Resource toResourceEntity(ResourceDtoRq dto);

    ResourceDtoRs toResourceDtoResponse(Resource entity);
}
