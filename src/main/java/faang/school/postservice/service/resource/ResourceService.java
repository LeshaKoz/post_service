package faang.school.postservice.service.resource;

import faang.school.postservice.model.entity.Resource;

import java.util.List;

public interface ResourceService {
    Resource save(Resource resource);

    Resource getResource(Long id);

    List<Resource> getResourcesByIds(List<Long> ids);
}