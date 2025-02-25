package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForNewsFeedDto {
    private Long id;
    private String content;
    private UserForNewsFeedResponseDto user;
}
