package faang.school.postservice.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaginationService {

    public <T> void processInBatches(
            List<T> items, int batchSize, Consumer<List<T>> processor
    ) {
        for (int i = 0; i < items.size(); i += batchSize) {
            List<T> batch = items.subList(i, Math.min(i + batchSize, items.size()));
            processor.accept(batch);
        }
    }

    public <T, R> List<R> processInParallel(
            List<T> items, int batchSize, Function<List<T>, Stream<R>> mapper
    ) {
        return items.stream()
                .collect(Collectors.groupingBy(item -> items.indexOf(item) / batchSize))
                .values()
                .parallelStream()
                .flatMap(mapper)
                .collect(Collectors.toList());
    }
}
