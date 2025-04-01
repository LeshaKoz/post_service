package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.post.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeInfo {

    private String postId;

    private int totalLikes;

    private List<LikeDto> likes;

}