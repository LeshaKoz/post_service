package faang.school.postservice.dto.post;


import java.io.Serializable;
import java.util.List;

public record HashtagResponseDto(String name, List<Long> postsIds) implements Serializable {

}
