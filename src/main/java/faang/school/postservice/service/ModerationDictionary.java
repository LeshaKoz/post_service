package faang.school.postservice.service;

import org.springframework.stereotype.Component;

@Component
public class ModerationDictionary {

    public boolean isTextAreCorrect(String text) {
        return Math.random() < 0.5;
    }
}
