package faang.school.postservice.dto.speller;

import lombok.Data;

import java.util.List;

@Data
public class SpellerDto {
    private int code;
    private int pos;
    private int row;
    private int col;
    private int len;
    private String word;
    private List<String> s;
}
