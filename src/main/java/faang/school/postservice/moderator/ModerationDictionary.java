package faang.school.postservice.moderator;

import faang.school.postservice.config.context.ModerationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final ModerationConfig moderationConfig;

    public int size() {
        return moderationConfig.getRudeWords().size();
    }

    public boolean isEmpty() {
        return moderationConfig.getRudeWords().isEmpty();
    }

    public boolean containsWord(String word) {
        return moderationConfig.getRudeWords().contains(word.toLowerCase());
    }

    public Iterator<String> iterator() {
        return moderationConfig.getRudeWords().iterator();
    }

    public Object[] toArray() {
        return moderationConfig.getRudeWords().toArray();
    }

    public String[] toArray(String[] a) {
        return moderationConfig.getRudeWords().toArray(a);
    }

    public boolean containsAll(Collection<String> collection) {
        return moderationConfig.getRudeWords().containsAll(collection.stream().map(String::toLowerCase).toList());
    }
}
