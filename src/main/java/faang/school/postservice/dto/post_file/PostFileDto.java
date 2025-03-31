package faang.school.postservice.dto.post_file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFileDto {
    private long id;
    private String key;
    private String name;
    private String type;
    private long postId;
}
