package faang.school.postservice.events;

import faang.school.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserBanEvent implements Event {
    private long userId;
    private boolean banned;
}
