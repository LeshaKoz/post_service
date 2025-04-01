package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResourceDtoRs {

    @NotNull
    private Long id;
    @NotNull
    private String key;
    private Long size;
    private String name;
    private String type;
    private Long postId;
    private LocalDateTime createdAt;
}
