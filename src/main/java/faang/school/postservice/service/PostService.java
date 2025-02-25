package faang.school.postservice.service;

import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.corrector.PostCorrector;
import faang.school.postservice.service.moderate.ModerationDictionary;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostCorrector postCorrector;
    private ExecutorService executor;
    private ModerationDictionary moderationDictionary;
    @Value("${moderation.batch-size}")
    private int batchSize;
    @Value("${post-service.thread-pool-size}")
    private int threadPoolSize;

    @PostConstruct
    private void init() {
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public Post findById(@NotNull Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Пост с id=%d не найден", id)));
    }

    public List<ReadPostDto> getFilteredPosts(PostFilterDto postFilterDto) {
        postValidator.validateFilterDto(postFilterDto);

        if (postFilterDto.authorId() != null) {
            return getPosts(postFilterDto.authorId(), postFilterDto.isPublished(), true);
        } else {
            return getPosts(postFilterDto.projectId(), postFilterDto.isPublished(), false);
        }
    }

    private List<ReadPostDto> getPosts(Long id, boolean published, boolean byAuthor) {
        List<Post> posts = byAuthor ? postRepository.findByAuthorId(id) : postRepository.findByProjectId(id);

        posts = posts.stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(published ? Post::getPublishedAt : Post::getCreatedAt).reversed())
                .toList();

        return postMapper.toDtoList(posts);
    }

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

    public void correctAllUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        posts.forEach(postCorrector::correctContentPost);
        postRepository.saveAll(posts);
    }

    public void moderateUnverifiedPosts() {
        int page = 0;
        List<Post> posts;

        do {
            posts = getUnverifiedPostsBatch(page);
            if (!posts.isEmpty()) {
                List<Post> finalPosts = posts;
                executor.submit(() -> processPosts(finalPosts));
            }
            page++;
        } while (posts.size() == batchSize);
    }

    private List<Post> getUnverifiedPostsBatch(int page) {
        Pageable pageable = PageRequest.of(page, batchSize);

        return postRepository.findUnverifiedPosts(pageable).getContent();
    }

    private void processPosts(List<Post> posts) {
        for (Post post : posts) {
            boolean containsBadWords = moderationDictionary.containsBadWords(post.getContent());
            post.setVerified(!containsBadWords);
            post.setVerifiedDate(LocalDateTime.now());
        }

        postRepository.saveAll(posts);
    }
}