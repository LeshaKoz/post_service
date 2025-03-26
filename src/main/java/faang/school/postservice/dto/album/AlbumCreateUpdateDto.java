package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AlbumCreateUpdateDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;

}
