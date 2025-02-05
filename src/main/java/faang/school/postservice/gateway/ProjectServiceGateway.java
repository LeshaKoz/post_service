package faang.school.postservice.gateway;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectServiceGateway {
    private final ProjectServiceClient projectServiceClient;

    public ProjectDto getProject(Long projectId) {
        ResponseEntity<ProjectDto> response = projectServiceClient.getProject(projectId);
        if (response.getBody() == null) {
            throw new ExternalServiceValidationException("Empty response from ProjectService for ID: " + projectId);
        }
        return response.getBody();
    }
}