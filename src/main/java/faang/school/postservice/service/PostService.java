package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.exception.DataAlreadyDeletedException;
import faang.school.postservice.exception.DataAlreadyExistException;
import faang.school.postservice.exception.UnpublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.PredicatedMap;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepositoryAdapter postRepositoryAdapter;

    public PostDTO createDraft(PostDTO draftDTO) {
        postValidator.validatedOwnerPost(draftDTO);
        Post draft = postRepositoryAdapter.save(postMapper.toEntity(draftDTO));

        log.info("Draft post was successfully created, draft id = {}", draft.getId());
        return postMapper.toDto(draft);
    }

    public PostDTO publishPost(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isPublished() || (post.getScheduledAt() != null &&
                post.getScheduledAt().isAfter(LocalDateTime.now()))) {
            throw new DataAlreadyExistException("This post has already been published or scheduled for publication");
        }

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("Can not publish a remote post");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        log.info("Post was successfully published, post id = {}", postId);
        return postMapper.toDto(post);
    }

    public PostDTO updatePost(PostDTO updatePost) {
        Post post = postValidator.findPostWithId(updatePost.id());
        postValidator.validateAuthorForUpdate(post, updatePost);

        post.setContent(updatePost.content());
        post.setUpdatedAt(LocalDateTime.now());

        log.info("Post was successfully updated, post id = {}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDTO deletePost(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }
        post.setDeleted(true);

        log.info("Post was successfully deleted, post id = {}", postId);
        return postMapper.toDto(post);
    }

    public PostDTO getPostById(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }

        if (post.getScheduledAt() != null) {
            throw new UnpublishedPostException("This post unpublished yet");
        }

        log.info("Post received successfully, post id = {}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDTO> getAllDraftsByAuthorId(long authorId) {
        postValidator.userOwnerOfThePost(authorId);

        List<PostDTO> allUserDrafts = getAllDrafts(authorId);

        log.info("Drafts received successfully, author id = {}", authorId);
        return allUserDrafts;
    }

    public List<PostDTO> getAllDraftsByProjectId(long projectId) {
        postValidator.projectOwnerOfThePost(projectId);

        List<PostDTO> allProjectDrafts = getAllDrafts(projectId);

        log.info("Drafts received successfully, project id = {}", projectId);
        return allProjectDrafts;
    }

    public List<PostDTO> getAllPostsByAuthorId(long authorId) {
        postValidator.userOwnerOfThePost(authorId);

        List<PostDTO> allUserPosts = getAllPosts(authorId);

        log.info("Post received successfully, author id = {}", authorId);
        return allUserPosts;
    }

    public List<PostDTO> getAllPostsByProjectId(long projectId) {
        postValidator.projectOwnerOfThePost(projectId);

        List<PostDTO> allProjectPosts = getAllPosts(projectId);

        log.info("Posts received successfully, project id = {}", projectId);
        return allProjectPosts;
    }

    private List<PostDTO> getAllDrafts(long ownerId) {
        return getPosts(ownerId, post -> !post.isPublished() && !post.isDeleted());
    }

    private List<PostDTO> getAllPosts(long ownerId) {
        return getPosts(ownerId, post -> post.isPublished() && !post.isDeleted());
    }

    private List<PostDTO> getPosts(long ownerId, Predicate<Post> filter) {
        return postRepositoryAdapter.findByAuthorId(ownerId)
                .stream()
                .filter(filter)
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }
}
