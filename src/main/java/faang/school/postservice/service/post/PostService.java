package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserContext userContext;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executorService;
    private final PostProperties postProperties;

    public PostReadDto createPostDraft(PostCreateDto dto) {
        validateCreateDraftDto(dto);

        Post post = postMapper.toEntity(dto);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostReadDto publishPost(long id) {
        Post post = getPostById(id);
        if (post.isPublished()) {
            throw new BusinessException("Пост уже опубликован");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto updatePost(long id, PostUpdateDto dto) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост удалён");
        }
        postMapper.updateEntityFromDto(dto, post);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto softDeletePost(long id) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост уже удален");
        }
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostReadDto> getAllDrafts(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllDraftsByAuthorId(id),
                () -> postRepository.findAllDraftsByProjectId(id)
        );
    }

    public List<PostReadDto> getAllPublished(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllPublishedByAuthorId(id),
                () -> postRepository.findAllPublishedByProjectId(id)
        );
    }

    public Post getPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден"));
    }

    public void moderatePosts() {
        concurrencyProcessPosts(
                postRepository::findAllNotVerified,
                this::moderatePostsBatch
        );
    }

    private List<Post> moderatePostsBatch(List<Post> posts) {
        LocalDateTime verifiedDate = LocalDateTime.now();
        return posts.stream()
                .filter(post -> moderationDictionary.isAllowed(post.getContent()))
                .peek(post -> {
                    post.setVerified(true);
                    post.setVerifiedDate(verifiedDate);
                })
                .toList();
    }

    private void concurrencyProcessPosts(
            Function<Pageable, Page<Post>> getPostsFunction,
            Function<List<Post>, List<Post>> processFunction
    ) {
        int pageSize = postProperties.getPageSize();
        Pageable firstPageable = PageRequest.of(0, pageSize);
        Page<Post> firstPage = getPostsFunction.apply(firstPageable);
        int totalPages = firstPage.getTotalPages();

        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Post> page = getPostsFunction.apply(pageable);
            concurrencyProcessPostsPages(page.getContent(), processFunction);
        }
    }

    private void concurrencyProcessPostsPages(
            List<Post> posts,
            Function<List<Post>, List<Post>> processFunction
    ) {
        int notVerifiedPostsSize = posts.size();
        int batchSize = postProperties.getBatchSize();
        List<CompletableFuture<List<Post>>> futuresList = new ArrayList<>();

        for (int i = 0; i < notVerifiedPostsSize; i += batchSize) {
            int end = Math.min(i + batchSize, notVerifiedPostsSize);
            List<Post> partition = posts.subList(i, end);
            futuresList.add(CompletableFuture.supplyAsync(
                    () -> processFunction.apply(partition),
                    executorService
            ));
        }

        List<Post> postsToSave = futuresList
                .stream()
                .flatMap(future -> future.join().stream())
                .toList();
        postRepository.saveAll(postsToSave);
    }

    private List<PostReadDto> getAllPostByCondition(
            PostOwnerType ownerType,
            Supplier<List<Post>> authorSupplier,
            Supplier<List<Post>> projetcSupplier
    ) {
        List<Post> postStream = switch (ownerType) {
            case AUTHOR:
                yield authorSupplier.get();
            case PROJECT:
                yield projetcSupplier.get();
        };
        return postStream.stream()
                .map(postMapper::toDto)
                .toList();
    }

    private void validateCreateDraftDto(PostCreateDto dto) {
        var authorId = dto.getAuthorId();
        var projectId = dto.getProjectId();

        if (authorId != null && projectId != null) {
            throw new BusinessException("Пост может создать либо автор, либо проект.");
        }

        if (authorId != null) {
            userContext.setUserId(authorId);
            if (userServiceClient.getUser(authorId) == null) {
                throw new EntityNotFoundException("Пользователь не найден");
            }
        } else if (projectId != null) {
            userContext.setUserId(projectId);
            if (projectServiceClient.getProject(projectId) == null) {
                throw new EntityNotFoundException("Проект не найден");
            }
        }
    }
}
