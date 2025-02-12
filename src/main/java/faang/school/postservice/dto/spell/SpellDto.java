package faang.school.postservice.dto.spell;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpellDto implements Serializable {

    @JsonProperty("pos")
    int replacementStartIndex;

    @JsonProperty("len")
    int correctWordLength;

    @JsonProperty("s")
    List<String> suggestions;
}
