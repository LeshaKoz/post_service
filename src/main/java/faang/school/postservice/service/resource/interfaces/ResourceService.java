package faang.school.postservice.service.resource.interfaces;

import faang.school.postservice.model.Resource;

import java.util.List;

public interface ResourceService {
    int getCountByPostId(long postId);

    Resource save(Resource resource);

    List<Resource> findAllByPostId(long postId);

    Resource getResource(long resourceId);

    void deleteResource(long resourceId);
}
