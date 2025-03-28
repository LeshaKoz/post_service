package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.interfaces.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserContext userContext;

    @Test
    public void testCreatePostDraft_Success() throws Exception {
        PostDto outputDto = PostDto.builder()
                .id(1L)
                .content("Valid content")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(postService.createPostDraft(any(PostDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Valid content\", \"authorId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Valid content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(postService, times(1)).createPostDraft(any(PostDto.class));
    }

    @Test
    public void testCreatePostDraft_EmptyContent_ThrowsException() throws Exception {
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(1L);

        mockMvc.perform(post("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The content of the post must not be empty"));

        mockMvc.perform(post("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\", \"authorId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The content of the post must not be empty"));

        verify(postService, never()).createPostDraft(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testPublishPost_Success() throws Exception {
        PostDto outputDto = PostDto.builder()
                .id(1L)
                .content("Draft content")
                .authorId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        when(postService.publishPost(any(PostDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/post-service/posts/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Draft content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.publishedAt").exists());

        verify(postService, times(1)).publishPost(any(PostDto.class));
    }

    @Test
    public void testPublishPost_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).publishPost(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testUpdatePost_Success() throws Exception {
        PostDto outputDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .published(false)
                .updatedAt(LocalDateTime.now())
                .build();

        when(postService.updatePost(any(PostDto.class))).thenReturn(outputDto);

        mockMvc.perform(put("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"content\": \"Updated content\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(postService, times(1)).updatePost(any(PostDto.class));
    }

    @Test
    public void testUpdatePost_EmptyContent_ThrowsException() throws Exception {
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(1L);

        mockMvc.perform(post("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The content of the post must not be empty"));

        mockMvc.perform(post("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\", \"authorId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The content of the post must not be empty"));

        verify(postService, never()).updatePost(any(PostDto.class));
    }

    @Test
    public void testUpdatePost_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(put("/post-service/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).updatePost(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testDeletePost_Success() throws Exception {
        PostDto outputDto = PostDto.builder()
                .id(1L)
                .content("Post to delete")
                .authorId(1L)
                .deleted(true)
                .updatedAt(LocalDateTime.now())
                .build();

        when(postService.deletePost(any(PostDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/post-service/posts/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Post to delete"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(postService, times(1)).deletePost(any(PostDto.class));
    }

    @Test
    public void testDeletePost_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).deletePost(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testGetPost_Success() throws Exception {
        PostDto outputDto = PostDto.builder()
                .id(1L)
                .content("Existing post")
                .authorId(1L)
                .published(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .publishedAt(LocalDateTime.now().minusHours(1))
                .build();

        when(postService.getPost(any(PostDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/post-service/posts/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Existing post"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.publishedAt").exists());

        verify(postService, times(1)).getPost(any(PostDto.class));
    }

    @Test
    public void testGetPost_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).getPost(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testGetAuthorPostDrafts_Success() throws Exception {
        List<PostDto> outputList = List.of(
                PostDto.builder()
                        .id(1L)
                        .content("Draft 1")
                        .authorId(1L)
                        .published(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(postService.getAuthorPostDrafts(any(PostDto.class))).thenReturn(outputList);

        mockMvc.perform(post("/post-service/posts/author/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft 1"))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].published").value(false));

        verify(postService, times(1)).getAuthorPostDrafts(any(PostDto.class));
    }

    @Test
    public void testGetAuthorPostDrafts_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/author/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).getAuthorPostDrafts(any(PostDto.class));
    }

    /***************************************************************************************************/
    @Test
    public void testGetProjectPostDrafts_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/project/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).getProjectPostDrafts(any(PostDto.class));
    }

    @Test
    public void testGetAuthorPosts_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/author/published")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).getAuthorPublishedPosts(any(PostDto.class));
    }

    @Test
    public void testGetProjectPosts_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(post("/post-service/posts/project/published")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be greater than zero"));

        verify(postService, never()).getProjectPublishedPosts(any(PostDto.class));
    }
}