package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testGetPostsByHashtag() {
        when(postRepository.findByHashtag(eq("hashtag"))).thenReturn(List.of(getPost()));

        List<PostResponseDto> actualResult = postService.getPostsByHashtag("hashtag");

        assertEquals(getExpectedResult(), actualResult);
    }

    private List<PostResponseDto> getExpectedResult() {
        return Stream.of(getPost())
                .map(postMapper::toDto)
                .toList();
    }

    private Post getPost() {
        return Post.builder()
                .id(1L)
                .hashtags(List.of(getHashtag()))
                .build();
    }

    private Hashtag getHashtag() {
        return Hashtag
                .builder()
                .name("hashtag")
                .build();
    }
}