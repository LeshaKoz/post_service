package faang.school.postservice.dto.user;

import java.util.List;


public record UsersBanEvent(
        List<Long> userIdsToBan
) {
}
