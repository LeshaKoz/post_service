package faang.school.postservice.dto.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarReadDto {
    private String word;
    @JsonProperty("s")
    private List<String> hints;
}
