package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlbumUpdateDto {
    @NotBlank(message = "ID не может быть пустым")
    @Positive(message = "ID должно быть положительным")
    private long id;
    @Size(max = 256)
    private String title;
    @Size(max = 4096)
    private String description;
}
