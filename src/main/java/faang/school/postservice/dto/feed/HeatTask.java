package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HeatTask {

    private List<Long> userIds;

    private String taskId;
}