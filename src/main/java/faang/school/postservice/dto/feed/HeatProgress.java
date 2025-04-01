package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeatProgress {

    private String taskId;

    private int processedUsers;

    private int totalUsers;

}