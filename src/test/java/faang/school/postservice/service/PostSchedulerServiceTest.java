package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSchedulerServiceTest {
    private ExecutorService executorService;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostSchedulerService postSchedulerService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(1);
        postSchedulerService = new PostSchedulerService(postRepository, executorService);
        ReflectionTestUtils.setField(postSchedulerService, "maxRetries", 3);
        ReflectionTestUtils.setField(postSchedulerService, "batchCount", 3);
    }

    @Test
    void testPublishScheduledPosts_NoPosts() {
        Page<Post> emptyPage = new PageImpl<>(List.of());
        when(postRepository.findReadyToPublish(any(PageRequest.class))).thenReturn(emptyPage);

        postSchedulerService.publishScheduledPosts(10);

        verify(postRepository, times(1)).findReadyToPublish(any(PageRequest.class));
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void shouldPublishScheduledPosts_whenPostsAreAvailable() throws Exception {
        Post post1 = new Post();
        post1.setAuthorId(1L);
        post1.setPublished(false);

        Post post2 = new Post();
        post2.setAuthorId(2L);
        post2.setPublished(false);

        List<Post> posts = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 2), posts.size());
        when(postRepository.findReadyToPublish(any(PageRequest.class))).thenReturn(postPage);

        postSchedulerService.publishScheduledPosts(2);

        verify(postRepository, times(1)).saveAll(argThat(savedPosts ->
                StreamSupport.stream(savedPosts.spliterator(), false)
                        .allMatch(post -> post.isPublished() && post.getPublishedAt() != null)
        ));
    }


}
