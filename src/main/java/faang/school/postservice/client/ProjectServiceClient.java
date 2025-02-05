package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.handler.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.url}",
        configuration = {FeignConfig.class, CustomErrorDecoder.class})
public interface ProjectServiceClient {
    @GetMapping("/project/{projectId}")
    ResponseEntity<ProjectDto> getProject(@PathVariable long projectId);

    @PostMapping("/projects")
    ResponseEntity<List<ProjectDto>> getProjectsByIds(@RequestBody List<Long> ids);
}
