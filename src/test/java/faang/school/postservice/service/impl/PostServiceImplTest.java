package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.filter.post.AuthorSpecification;
import faang.school.postservice.filter.post.PostSpecificationFilter;
import faang.school.postservice.filter.post.ProjectSpecification;
import faang.school.postservice.filter.post.PublishedSpecification;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @Mock
    private PostServiceValidator postServiceValidatorMock;
    @Mock
    private PostRepository postRepositoryMock;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    ExecutorService executorService;
    @InjectMocks
    private PostServiceImpl postService;
    private PostCreateRequestDto postCreateRequestDto;
    private PostUpdateRequestDto postUpdateRequestDto;
    private final List<PostSpecificationFilter> postSpecificationFilters = new ArrayList<>();

    @BeforeEach
    void setUp() {

        PostSpecificationFilter authorSpec = new AuthorSpecification();
        PostSpecificationFilter projectSpec = new ProjectSpecification();
        PostSpecificationFilter publishedSpec = new PublishedSpecification();

        postSpecificationFilters.add(authorSpec);
        postSpecificationFilters.add(projectSpec);
        postSpecificationFilters.add(publishedSpec);

        postService = new PostServiceImpl(
                postRepositoryMock,
                postServiceValidatorMock,
                postMapper,
                postSpecificationFilters,
                executorService
        );

        postCreateRequestDto = PostCreateRequestDto.builder()
                .content("Test content")
                .authorId(111L)
                .build();

        postUpdateRequestDto = PostUpdateRequestDto.builder()
                .content("Test content")
                .build();
    }

    @Test
    @DisplayName("Test create draft")
    void testCreatePostDraft() {
        Post post = Post.builder()
                .id(123L)
                .content(postCreateRequestDto.content())
                .authorId(postCreateRequestDto.authorId())
                .projectId(postCreateRequestDto.projectId())
                .build();
        Mockito.when(postRepositoryMock.save(Mockito.any())).thenReturn(post);
        PostResponseDto resultPostResponseDto = postService.createPostDraft(postCreateRequestDto);
        Assertions.assertEquals(postCreateRequestDto.authorId(), resultPostResponseDto.authorId());
        Assertions.assertEquals(postCreateRequestDto.projectId(), resultPostResponseDto.projectId());
        Assertions.assertEquals(postCreateRequestDto.content(), resultPostResponseDto.content());
    }

    @Test
    @DisplayName("Test publish draft")
    void testPublishPostDraft() {
        Long postId = 123L;
        Post postBefore = Post.builder()
                .id(postId)
                .content(postCreateRequestDto.content())
                .authorId(postCreateRequestDto.authorId())
                .projectId(postCreateRequestDto.projectId())
                .build();
        Post postAfter = Post.builder()
                .id(postId)
                .content(postCreateRequestDto.content())
                .authorId(postCreateRequestDto.authorId())
                .projectId(postCreateRequestDto.projectId())
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();
        Mockito.when(postRepositoryMock.findById(postId)).thenReturn(Optional.ofNullable(postBefore));
        Mockito.when(postRepositoryMock.save(Mockito.any())).thenReturn(postAfter);
        PostResponseDto publishedPostResponseDto = postService.publishPostDraft(postId);
        Mockito.verify(postRepositoryMock, Mockito.times(1)).save(Mockito.any());
        Assertions.assertTrue(publishedPostResponseDto.isPublished());
        Assertions.assertNotNull(publishedPostResponseDto.publishedAt());
    }

    @Test
    @DisplayName("Test update post")
    void testUpdatePost() {
        Long postId = 123L;
        String newContent = "edited content";
        Post postBefore = Post.builder()
                .id(postId)
                .content(postCreateRequestDto.content())
                .authorId(postCreateRequestDto.authorId())
                .projectId(postCreateRequestDto.projectId())
                .build();
        Post postAfter = Post.builder()
                .id(postId)
                .content(newContent)
                .authorId(postCreateRequestDto.authorId())
                .projectId(postCreateRequestDto.projectId())
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();
        Mockito.when(postRepositoryMock.findById(postId)).thenReturn(Optional.ofNullable(postBefore));
        Mockito.when(postRepositoryMock.save(Mockito.any())).thenReturn(postAfter);
        PostResponseDto updatedPostResponseDto = postService.updatePost(postId, postUpdateRequestDto);
        Mockito.verify(postRepositoryMock, Mockito.times(1)).save(Mockito.any());
        Assertions.assertEquals(newContent, updatedPostResponseDto.content());
    }

    @Test
    @DisplayName("Test delete post")
    void testDeletePost() {
        Long postId = 123L;
        Mockito.when(postRepositoryMock.findById(postId)).thenReturn(Optional.of(new Post()));
        postService.deletePost(123L);
        Mockito.verify(postRepositoryMock, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Test get post")
    void testGetPost() {
        Long postId = 123L;
        Mockito.when(postRepositoryMock.findById(postId)).thenReturn(Optional.of(new Post()));
        postService.getPost(postId);
        Mockito.verify(postRepositoryMock, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Test get posts by filter")
    void testGetPostsByFilter() {
        long projectId = 222L;
        PostFilterDto draftsProjectFilter = PostFilterDto.builder().isPublished(false).projectId(projectId).build();
        postService.findAllByFilter(draftsProjectFilter);
        Mockito.verify(postRepositoryMock, Mockito.times(1)).findAll(Mockito.any());
    }

}