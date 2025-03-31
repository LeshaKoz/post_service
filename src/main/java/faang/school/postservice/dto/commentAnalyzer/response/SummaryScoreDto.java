package faang.school.postservice.dto.commentAnalyzer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryScoreDto {
    private float value;
    private String type;
}
