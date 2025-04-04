package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private PostService postService;

    @Test
    void moderatePostsShouldMarkPostAsVerifiedWhenNoBadWords() {

        Post cleanPost = new Post();
        cleanPost.setId(1L);
        cleanPost.setAuthorId(101L);
        cleanPost.setContent("Чистый контент без плохих слов");

        when(moderationDictionary.containsBadWord(cleanPost.getContent())).thenReturn(false);

        postService.moderatePosts(List.of(cleanPost));

        assertTrue(cleanPost.isVerified());
        assertNotNull(cleanPost.getVerifiedAt());
        verify(postRepository).saveAll(List.of(cleanPost));
    }

    @Test
    void moderatePostsShouldMarkPostAsUnverifiedWhenBadWordsFound() {

        Post badPost = new Post();
        badPost.setId(2L);
        badPost.setAuthorId(102L);
        badPost.setContent("Этот пост содержит запрещённое слово");

        when(moderationDictionary.containsBadWord(badPost.getContent())).thenReturn(true);

        postService.moderatePosts(List.of(badPost));

        assertFalse(badPost.isVerified());
        assertNotNull(badPost.getVerifiedAt());
        verify(postRepository).saveAll(List.of(badPost));
    }
}
