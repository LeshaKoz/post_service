package faang.school.postservice.dto.heat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
class HeatingBatchTask implements Serializable {

    private int offset;

    private int limit;
}