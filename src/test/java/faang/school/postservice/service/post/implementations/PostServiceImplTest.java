package faang.school.postservice.service.post.implementations;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.interfaces.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper;

    @Test
    void createPostDraft() {
    }

    @Test
    void publicPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void getPost() {
    }

    @Test
    public void testGetAuthorPostDraftsSuccess() throws Exception {

    }

    @Test
    void getProjectPostDrafts() {
    }

    @Test
    void getAuthorPublishedPosts() {
    }

    @Test
    void getProjectPublishedPosts() {
    }
}