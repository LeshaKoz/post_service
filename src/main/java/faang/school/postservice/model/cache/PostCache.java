package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RMap;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "posts")
public class PostCache implements Serializable {
    @Id
    private long postId;
    private long authorId;
    private String content;
    private List<Long> likesId;
    private LinkedHashSet<String> comments;
    private long numLikes;
    private long numViews;
    private RMap<String, Integer> version;

    public void incrementLikes() {
        numLikes++;
    }

    public void incrementViews() {
        numViews++;
    }
}
