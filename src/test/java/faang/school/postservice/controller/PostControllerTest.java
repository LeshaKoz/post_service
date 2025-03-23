package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostController postController;

    @Test
    void getPost() {
        Long postId = 123765L;
        Post post = new Post();
        PostDto dto = new PostDto();

        Mockito.when(postService.getPost(postId)).thenReturn(post);
        Mockito.when(postMapper.toDto(Mockito.any(Post.class))).thenReturn(dto);

        assertEquals(dto, postController.getPost(postId));
        Mockito.verify(postService, Mockito.times(1)).getPost(Mockito.any());
        Mockito.verify(postMapper, Mockito.times(1)).toDto(Mockito.any());
    }
}