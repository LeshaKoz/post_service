package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "author")
public class UserCache implements Serializable {
    @Id
    private long userId;
    private String userName;
}
