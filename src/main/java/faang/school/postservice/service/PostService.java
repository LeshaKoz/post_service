package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.corrector.PostCorrector;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCorrector postCorrector;

    public void correctAllUnpublishedPosts() {
        log.info("Начало запланированного события");

        List<Post> posts = postRepository.findReadyToPublish();
        posts.forEach(postCorrector::correctContentPost);
        postRepository.saveAll(posts);

        log.info("Конец запланированного события");
    }

    public Post findById(@NotNull Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Пост с id=%d не найден", id)));
    }
}
