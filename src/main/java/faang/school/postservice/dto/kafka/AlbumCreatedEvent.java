package faang.school.postservice.dto.kafka;

import faang.school.postservice.enums.kafka.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumCreatedEvent {

    private long userId;

    private AchievementType title;

}