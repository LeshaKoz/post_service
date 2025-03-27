package faang.school.postservice.service.resource.implementations;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.interfaces.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private static final String RESOURCE = "Resource";
    private final ResourceRepository resourceRepository;

    @Override
    public int getCountByPostId(long postId) {
        return resourceRepository.countByPostId(postId);
    }

    @Override
    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public List<Resource> findAllByPostId(long postId) {
        return resourceRepository.findAllByPostId(postId);
    }

    @Override
    public Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(RESOURCE, resourceId));
    }

    @Override
    public void deleteResource(long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}
