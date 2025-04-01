package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.post.PostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FeedResponse {

    private List<PostResponseDto> posts;

    private String lastPostId;

    private boolean hasMore;
}