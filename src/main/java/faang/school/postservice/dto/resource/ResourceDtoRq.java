package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDtoRq {
    private String name;
    private String type;
    private Long postId;
    @NotNull
    private String key;
}
