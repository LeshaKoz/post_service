package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Users", timeToLive = 90_000)
public class UserCache implements Serializable {
  @Id
  private Long id;

  @NotBlank(message = "Name should not be blank")
  private String username;

  @Email(message = "Email must be in right format")
  private String email;

}