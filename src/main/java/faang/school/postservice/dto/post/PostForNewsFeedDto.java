package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentForNewsFeedDto;
import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
public class PostForNewsFeedDto {
    private Long id;
    private String content;
    private UserForNewsFeedResponseDto user;
    private Integer likeCount;
    private Long viewCount;
    private List<CommentForNewsFeedDto> comments;
    private LocalDateTime createdAt;
}
