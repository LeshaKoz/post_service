package faang.school.postservice.service.resource;

import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.model.entity.Resource;
import faang.school.postservice.repository.entity.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;

    @Override
    public Resource save(@NotNull Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Resource getResource(@Positive Long id) {
        return resourceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ExceptionMessage.OBJECT_IS_NOT_FOUND.getMessage()));
    }

    @Override
    public List<Resource> getResourcesByIds(List<Long> ids) {
        if (ids == null){
            return null;
        }
        return resourceRepository.findAllById(ids);
    }
}
