package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с постами.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #getPostEntity(long)} - возвращает сущность поста по указанному ID</li>
 * </ul>
 *
 * @author gulnaz21
 * <p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    /**
     * Возвращает пост по его идентификатору.
     *
     * @param postId Идентификатор поста.
     * @return Найденный пост.
     * @throws EntityNotFoundException Если пост с указанным идентификатором не найден.
     */
    public Post getPostEntity(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Ошибка: Пост с id {} не найден", postId);
            return new EntityNotFoundException(String.format("Post not found with id: %d", postId));
        });
    }
}
