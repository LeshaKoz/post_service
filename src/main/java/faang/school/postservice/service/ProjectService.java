package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public ProjectDto getProjectById(long projectId) {
        try {
            userContext.setUserId(projectId);
            return projectServiceClient.getProject(projectId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Проект с ID " + projectId + " не найден ");
        }
    }
}
