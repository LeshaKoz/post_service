package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.TreeSet;

@RedisHash(value = "posts")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private long authorId;
    private LocalDateTime createdAt;
    private long countOfViews;
    private long countOfLikes;
    private TreeSet<CommentCache> comments;

    @TimeToLive
    private long ttl;

    public void addComment(CommentCache comment, int maxComments) {
        if (comments == null) {
            comments = new TreeSet<>();
        }

        comments.add(comment);
        if (comments.size() > maxComments) {
            comments.pollLast();
        }
    }
}
