package faang.school.postservice.jobs;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class PostCorrecterTest {

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostCorrecter postCorrecter;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        Field limitField = PostCorrecter.class.getDeclaredField("limit");
        limitField.setAccessible(true);
        limitField.set(postCorrecter, 10);
    }

    @Test
    void postCorrecterJob_whenPostsFound_shouldCorrectPosts() {
        Post post = new Post();
        when(postRepository.findPostsByPublishedIsFalseAndAiCheckedIsFalse(PageRequest.of(0, 10))).thenReturn(List.of(post));

        postCorrecter.postCorrecterJob();

        verify(postService, times(1)).grammarCorrectionPost(post);
        verify(postRepository, times(1)).findPostsByPublishedIsFalseAndAiCheckedIsFalse(PageRequest.of(0, 10));
    }

    @Test
    void postCorrecterJob_whenNoPostsFound_shouldNotCorrectPosts() {
        when(postRepository.findPostsByPublishedIsFalseAndAiCheckedIsFalse(PageRequest.of(0, 10))).thenReturn(Collections.emptyList());

        postCorrecter.postCorrecterJob();

        verify(postService, never()).grammarCorrectionPost(any());
        verify(postRepository, times(1)).findPostsByPublishedIsFalseAndAiCheckedIsFalse(PageRequest.of(0, 10));
    }

    @Test
    void postCorrecterJob_whenLimitIsZero_shouldNotCorrectPosts() throws NoSuchFieldException, IllegalAccessException {
        Field limitField = PostCorrecter.class.getDeclaredField("limit");
        limitField.setAccessible(true);
        limitField.set(postCorrecter, 0);

        postCorrecter.postCorrecterJob();

        verify(postService, never()).grammarCorrectionPost(any());
        verify(postRepository, never()).findPostsByPublishedIsFalseAndAiCheckedIsFalse(any());
    }
}