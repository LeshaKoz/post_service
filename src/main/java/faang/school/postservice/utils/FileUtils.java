package faang.school.postservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileUtils {
    private final ResourceLoader resourceLoader;

    public List<String> getAllLines(String filePath) {
        try {
            Path path = Paths.get(resourceLoader.getResource(
                    "classpath:" + filePath
            ).getURI());
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка открытия файла", e);
        }
    }
}
