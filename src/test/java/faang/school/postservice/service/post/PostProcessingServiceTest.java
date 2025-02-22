package faang.school.postservice.service.post;

import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.GrammarService;
import faang.school.postservice.service.PaginationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostProcessingServiceTest {
    @Spy
    private PaginationService paginationService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Spy
    private PostProperties postProperties;
    @Mock
    private GrammarService grammarService;
    @InjectMocks
    private PostProcessingService postProcessingService;

    @Test
    void testModeratePostsSuccessCase() {
        int pageSize = 2;
        postProperties.getModeration().setPageSize(pageSize);
        postProperties.getModeration().setBatchSize(1);
        var firstPagePosts = List.of(
                createPostWithContent("Content"),
                createPostWithContent("Content")
        );
        var secondPagePosts = List.of(
                createPostWithContent("Content"),
                createPostWithContent("Bad Content")

        );
        Pageable firstPageable = PageRequest.of(0, pageSize);
        Pageable secondPageable = PageRequest.of(1, pageSize);
        Page<Post> firstPage = new PageImpl<>(firstPagePosts, firstPageable, 4);
        Page<Post> secondPage = new PageImpl<>(secondPagePosts, secondPageable, 4);

        when(postRepository.findAllNotVerified(firstPageable)).thenReturn(firstPage);
        when(postRepository.findAllNotVerified(secondPageable)).thenReturn(secondPage);
        when(moderationDictionary.isAllowed("Content")).thenReturn(true);
        when(moderationDictionary.isAllowed("Bad Content")).thenReturn(false);
        ArgumentCaptor<List<Post>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        postProcessingService.moderatePosts();

        verify(postRepository, times(2))
                .saveAll(argumentCaptor.capture());
        List<Post> capturedPosts1 = argumentCaptor.getAllValues().get(0);
        List<Post> capturedPosts2 = argumentCaptor.getAllValues().get(1);
        assertEquals(2, capturedPosts1.size());
        assertEquals(1, capturedPosts2.size());
        assertTrue(isVerified(capturedPosts2.get(0)));
        assertTrue(capturedPosts1.stream().allMatch(this::isVerified));
    }

    @Test
    void testCheckGrammarSuccessCase() {
        int pageSize = 2;
        String rightText = "the quick brown fox";
        String wrongText = "teh quick brown fox";
        postProperties.getGrammar().setPageSize(pageSize);
        postProperties.getGrammar().setBatchSize(1);
        var firstPagePosts = List.of(
                createPostWithContent(wrongText),
                createPostWithContent(rightText)
        );
        var secondPagePosts = List.of(
                createPostWithContent(rightText),
                createPostWithContent(wrongText)

        );
        Pageable firstPageable = PageRequest.of(0, pageSize);
        Pageable secondPageable = PageRequest.of(1, pageSize);
        Page<Post> firstPage = new PageImpl<>(firstPagePosts, firstPageable, 4);
        Page<Post> secondPage = new PageImpl<>(secondPagePosts, secondPageable, 4);

        when(postRepository.findAllNotPublishedAndVerifiedTrue(firstPageable))
                .thenReturn(firstPage);
        when(postRepository.findAllNotPublishedAndVerifiedTrue(secondPageable))
                .thenReturn(secondPage);
        when(grammarService.correctText(rightText)).thenReturn(rightText);
        when(grammarService.correctText(wrongText)).thenReturn(rightText);
        ArgumentCaptor<List<Post>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        postProcessingService.checkGrammar();

        verify(postRepository, times(2))
                .saveAll(argumentCaptor.capture());
        assertTrue(argumentCaptor.getAllValues().stream()
                .flatMap(List::stream)
                .allMatch(post -> post.getContent().equals(rightText)));
    }

    private boolean isVerified(Post post) {
        return post.isVerified()
                && post.getVerifiedDate() != null
                && post.getContent().equals("Content");
    }

    private Post createPostWithContent(String content) {
        return Post.builder().content(content).build();
    }
}
