package faang.school.postservice.validator;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {

    private final PostRepository postRepository;

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> {
                    log.warn("Post with id {} not found", postId);
                    return new EntityNotFoundException("Post not found");
                }
        );
    }
}
