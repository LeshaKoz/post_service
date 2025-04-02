package faang.school.postservice.model.cache;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RedisHash("authors")
@Builder
public class CacheAuthor {
    public static final String USER_PREFIX = "user_";
    public static final String PROJECT_PREFIX = "project_";

    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private Long userId;
    private Long projectId;
    private String authorName;

    @TimeToLive
    private Long timeToLeave;

    public void setUserId(Long id) {
        this.id = "user_" + id.toString();
        this.userId = id;
    }

    public void setProjectId(Long id) {
        this.id = "project_" + id.toString();
        this.projectId = id;
    }

}
