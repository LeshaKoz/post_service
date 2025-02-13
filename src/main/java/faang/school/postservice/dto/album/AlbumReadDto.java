package faang.school.postservice.dto.album;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.postservice.dto.post.PostReadDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlbumReadDto {
    private long id;
    @NotEmpty(message = "Название альбома не может быть пустым!")
    private String title;
    @NotEmpty(message = "Описание альбома не может быть пустым!")
    private String description;
    private List<PostReadDto> posts;
}
