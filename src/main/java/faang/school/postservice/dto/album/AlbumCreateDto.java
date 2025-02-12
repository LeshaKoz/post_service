package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlbumCreateDto {
    @NotBlank(message = "Название альбома не должно быть пустым!")
    @Size(max = 256, message = "Название альбома не должно превышать 256 символов!")
    private String title;
    @NotBlank(message = "Описание альбома не должно быть пустым!")
    @Size(max = 4096, message = "Описание альбома не должно превышать 4096 символов!")
    private String description;
}
