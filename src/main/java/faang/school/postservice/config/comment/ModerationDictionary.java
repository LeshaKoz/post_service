package faang.school.postservice.config.comment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class ModerationDictionary {

    @Value("${moderation.dictionary}")
    private List<String> dictionary;


}
