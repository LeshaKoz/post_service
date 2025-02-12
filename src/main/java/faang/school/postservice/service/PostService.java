package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.service.corrector.PostCorrector;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public ReadPostDto create(CreatePostDto createPostDto) {
        postValidator.validateDraftPost(createPostDto);

        Post post = postMapper.toEntity(createPostDto);
        post.setPublished(false);

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    public ReadPostDto getPost(long postId) {
        Post post = findById(postId);
        if (post.isDeleted()) {
            throw new EntityNotFoundException(format("Пост с id=%d не найден", postId));
        }
        return postMapper.toDto(post);
    }

    @Transactional
    public ReadPostDto update(long postId, UpdatePostDto updatePostDto) {
        Post post = findById(postId);

        post.setContent(updatePostDto.content());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Transactional
    public ReadPostDto delete(long id) {
        Post post = findById(id);
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    @Transactional
    public ReadPostDto publish(long id) {
        Post post = findById(id);

        postValidator.validateNotPublished(post);
        postValidator.validateNotDeleted(post);
        postValidator.validatePostAuthorExist(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }
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

    private List<ReadPostDto> getPosts(Long id, boolean published, boolean byAuthor) {
        List<Post> posts = byAuthor ? postRepository.findByAuthorId(id) : postRepository.findByProjectId(id);

        posts = posts.stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(published ? Post::getPublishedAt : Post::getCreatedAt).reversed())
                .toList();

        return postMapper.toDtoList(posts);
    }

    public List<ReadPostDto> getFilteredPosts(PostFilterDto postFilterDto) {

        postValidator.validateFilterDto(postFilterDto);

        if (postFilterDto.authorId() != null) {
            return getPosts(postFilterDto.authorId(), postFilterDto.isPublished(), true);
        } else {
            return getPosts(postFilterDto.projectId(), postFilterDto.isPublished(), false);
        }
    }
}