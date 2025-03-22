package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Сервисный класс `PostService` для управления постами.
 * Обеспечивает создание, обновление, публикацию и удаление постов.
 *
 * <p>Основные методы:
 * <ul>
 *     <li>{@link #createDraft(PostCreateDto)} - создание черновика поста.</li>
 *     <li>{@link #publishPost(long)} - публикация поста.</li>
 *     <li>{@link #updatePost(PostUpdateDto, long)} - обновление поста.</li>
 *     <li>{@link #softDeletePost(long)} - мягкое удаление поста.</li>
 *     <li>{@link #getPost(long)} - получение поста по ID.</li>
 *     <li>{@link #getUserDraft(long)} - получение черновиков пользователя.</li>
 *     <li>{@link #getProjectDraft(long)} - получение черновиков проекта.</li>
 *     <li>{@link #getAuthorPublishedPost(long)} - получение опубликованных постов автора.</li>
 *     <li>{@link #getProjectPublishedPost(long)} - получение опубликованных постов проекта.</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    /**
     * Создает черновик поста на основе переданного DTO.
     *
     * @param postCreateDto DTO с данными для создания поста.
     * @return {@link PostViewDto} - DTO с данными созданного поста.
     */
    public PostViewDto createDraft(@NotNull PostCreateDto postCreateDto) {
        Post post = postMapper.createDtoToEntity(postCreateDto);
        post = postRepository.save(post);

        return postMapper.toViewDto(post);
    }

    /**
     * Публикует пост с указанным ID.
     *
     * @param postId ID поста для публикации.
     * @return {@link PostViewDto} - DTO с данными опубликованного поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     * @throws DataValidationException если пост уже опубликован.
     */
    @Transactional
    public PostViewDto publishPost(long postId) {
        Post publishPost = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID {} not found", postId);
            return new EntityNotFoundException("Post not found with id: " + postId);
        });

        if(publishPost.isPublished()){
            log.warn("Post with ID {} is already published", postId);
            throw new DataValidationException("Post is already published");
        }

        publishPost.setPublished(true);
        publishPost.setPublishedAt(LocalDateTime.now());

        return postMapper.toViewDto(publishPost);
    }

    /**
     * Обновляет пост с указанным ID на основе переданного DTO.
     *
     * @param postUpdateDto DTO с данными для обновления поста.
     * @param postId ID поста для обновления.
     * @return {@link PostViewDto} - DTO с обновленными данными поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     */
    @Transactional
    public PostViewDto updatePost(@NotNull PostUpdateDto postUpdateDto, long postId) {
        Post oldPost = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID {} not found", postId);
            return new EntityNotFoundException("Post not found with id: " + postId);
        });

        postMapper.update(postUpdateDto, oldPost);

        return postMapper.toViewDto(oldPost);
    }

    /**
     * Выполняет мягкое удаление поста с указанным ID.
     *
     * @param postId ID поста для удаления.
     * @return {@link PostViewDto} - DTO с данными удаленного поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     * @throws DataValidationException если пост уже удален.
     */
    @Transactional
    public PostViewDto softDeletePost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID {} not found", postId);
            return new EntityNotFoundException("Post not found with id: " + postId);
        });

        if (post.isDeleted()) {
            throw new DataValidationException("Post is already deleted");
        }
        post.setDeleted(true);

        return postMapper.toViewDto(post);
    }

    /**
     * Возвращает пост по указанному ID.
     *
     * @param postId ID поста.
     * @return {@link PostViewDto} - DTO с данными поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     */
    public PostViewDto getPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID {} not found", postId);
            return new EntityNotFoundException("Post not found with id: " + postId);
        });

        return postMapper.toViewDto(post);
    }

    /**
     * Возвращает список черновиков пользователя с указанным ID.
     *
     * @param userId ID пользователя.
     * @return {@link List<PostViewDto>} - список DTO с данными черновиков пользователя.
     */
    public List<PostViewDto> getUserDraft(long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post-> !(post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает список черновиков проекта с указанным ID.
     *
     * @param projectId ID проекта.
     * @return {@link List<PostViewDto>} - список DTO с данными черновиков проекта.
     */
    public List<PostViewDto> getProjectDraft(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post-> !(post.isDeleted()))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();

    }

    /**
     * Возвращает список опубликованных постов автора с указанным ID.
     *
     * @param userId ID автора.
     * @return {@link List<PostViewDto>} - список DTO с данными опубликованных постов автора.
     */
    public List<PostViewDto> getAuthorPublishedPost(long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post-> !(post.isDeleted()))
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает список опубликованных постов проекта с указанным ID.
     *
     * @param projectId ID проекта.
     * @return {@link List<PostViewDto>} - список DTO с данными опубликованных постов проекта.
     */
    public List<PostViewDto> getProjectPublishedPost(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post-> !(post.isDeleted()))
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }
}
