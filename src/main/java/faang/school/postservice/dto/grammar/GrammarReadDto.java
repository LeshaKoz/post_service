package faang.school.postservice.dto.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarReadDto {
    private boolean status;
    @JsonProperty("response.corrected")
    private String corrected;
    @JsonProperty("error_code")
    private int errorCode;
    private String description;
}
