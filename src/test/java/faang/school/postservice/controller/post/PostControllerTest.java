package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest
class PostControllerTest {

    @Test
    void getAuthorPostDrafts() {
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(1L);

        Post post1 = new Post();
        post1.setPublished(false);
        post1.setDeleted(false);
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));
        Post post2 = new Post();
        post2.setPublished(false);
        post2.setDeleted(false);
        post2.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(post1, post2));
        //when(postMapper.toDto(post2)).thenReturn(new PostDto(2L, "draft2"));
        //when(postMapper.toDto(post1)).thenReturn(new PostDto(1L, "draft1"));

        mockMvc.perform(get("/posts/author/{authorId}/drafts", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[1].userId").value(1L));
    }
}