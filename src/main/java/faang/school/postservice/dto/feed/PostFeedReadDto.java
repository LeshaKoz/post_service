package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFeedReadDto {
    private long id;
    private Long authorId;
    private String username;
    private String content;
    private int likesCount;
    private LocalDateTime createdAt;
}
