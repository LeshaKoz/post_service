package faang.school.postservice.dto.post;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class HashtagResponseDto {

    private String name;

    private List<Long> postsIds;

}
