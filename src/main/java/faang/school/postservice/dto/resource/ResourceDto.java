package faang.school.postservice.dto.resource;

import faang.school.postservice.messages.ValidationMessages;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDto {
    @NotEmpty(message = ValidationMessages.validationKeyNotEmpty)
    @Size(max = 50, message = ValidationMessages.validationKeyMaxLength)
    private String key;
    private long size;
    @NotEmpty(message = ValidationMessages.validationNameNotEmpty)
    @Size(max = 150, message = ValidationMessages.validationNameMaxLength)
    private String name;
    @NotEmpty(message = ValidationMessages.validationTypeNotEmpty)
    @Size(max = 50, message = ValidationMessages.validationTypeMaxLength)
    private String type;
    @NotNull(message = ValidationMessages.validationPostIdNotNull)
    private Long postId;
}
