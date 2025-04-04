package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Компонент для загрузки и предоставления доступа к набору нецензурных слов для модерации контента.
 * <p>
 * Данный класс загружает словарь нецензурных слов из файла в classpath при инициализации
 * и предоставляет методы для доступа к загруженным словам. Путь к файлу словаря настраивается
 * через механизм инъекции свойств Spring.
 * </p>

 */
@Slf4j
@Component
public class ModerationDictionary {

    private final Set<String> profanityWord = new HashSet<>();

    @Value("${moderation.dictionary.path}")
    private String dictionaryPath;

    /**
     * Инициализирует словарь, загружая слова из файла.
     * Принудительно завершает загрузку программы если не может подтянуть словарь
     *
     */
    @PostConstruct
    public void init() {
        try {
            loadDictionary();
        } catch (IOException e) {
            log.error("CRITICAL: Failed to load moderation dictionary. Shutting down.", e);
            System.exit(1);
        }
    }

    /**
     * Загружает слова из файла словаря в множество profanityWord.
     * Каждая строка файла представляет собой отдельное слово.
     * Пустые строки и пробелы игнорируются.
     *
     * @throws IOException если произошла ошибка при чтении файла
     */
    private void loadDictionary() throws IOException {
        log.info("Loading moderation dictionary");
        ClassPathResource resource = new ClassPathResource(dictionaryPath);
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    profanityWord.add(line.trim().toLowerCase());
                }
            }
        }
        log.info("Loaded {} profanity word", profanityWord.size());
    }

    /**
     * Возвращает копию множества нецензурных слов.
     *
     * @return неизменяемое множество нецензурных слов
     */
    public Set<String> getProfanityWord() {
        return new HashSet<>(profanityWord);
    }
}