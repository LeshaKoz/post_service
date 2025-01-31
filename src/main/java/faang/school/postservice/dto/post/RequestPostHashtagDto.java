package faang.school.postservice.dto.post;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class RequestPostHashtagDto {
    @NonNull
    List<String> hashtags;
}