package faang.school.postservice.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashtagDto {
    private Long id;
    private String name;
    private String createdAt;
}
