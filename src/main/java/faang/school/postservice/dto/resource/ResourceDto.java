package faang.school.postservice.dto.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.model.ResourceStatus;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ResourceDto {
    Long id;
    String size;
    String name;
    String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

    ResourceStatus status;
}