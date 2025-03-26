package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostMapperTest {
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void testToEntity() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(2L);
        postDto.setProjectId(3L);
        postDto.setContent("Test content");
        postDto.setPublished(true);
        postDto.setDeleted(false);
        postDto.setCreatedAt(LocalDateTime.of(2025, 3, 1, 10, 0));
        postDto.setPublishedAt(LocalDateTime.of(2025, 3, 2, 12, 0));
        postDto.setUpdatedAt(LocalDateTime.of(2025, 3, 3, 15, 0));

        Post post = postMapper.toEntity(postDto);

        assertNotNull(post);
        assertEquals(1L, post.getId());
        assertEquals(2L, post.getAuthorId());
        assertEquals(3L, post.getProjectId());
        assertEquals("Test content", post.getContent());
        assertTrue(post.isPublished());
        assertFalse(post.isDeleted());
        assertEquals(LocalDateTime.of(2025, 3, 1, 10, 0), post.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 3, 2, 12, 0), post.getPublishedAt());
        assertEquals(LocalDateTime.of(2025, 3, 3, 15, 0), post.getUpdatedAt());
    }

    @Test
    void testToDto() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(2L);
        post.setProjectId(3L);
        post.setContent("Test content");
        post.setPublished(true);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.of(2025, 3, 1, 10, 0));
        post.setPublishedAt(LocalDateTime.of(2025, 3, 2, 12, 0));
        post.setUpdatedAt(LocalDateTime.of(2025, 3, 3, 15, 0));

        PostDto postDto = postMapper.toDto(post);

        assertNotNull(postDto);
        assertEquals(1L, postDto.getId());
        assertEquals(2L, postDto.getAuthorId());
        assertEquals(3L, postDto.getProjectId());
        assertEquals("Test content", postDto.getContent());
        assertTrue(postDto.isPublished());
        assertFalse(postDto.isDeleted());
        assertEquals(LocalDateTime.of(2025, 3, 1, 10, 0), postDto.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 3, 2, 12, 0), postDto.getPublishedAt());
        assertEquals(LocalDateTime.of(2025, 3, 3, 15, 0), postDto.getUpdatedAt());
    }

    @Test
    void testToEntities() {
        PostDto postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setContent("Content 1");

        PostDto postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setContent("Content 2");

        List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

        List<Post> posts = postMapper.toEntities(postDtos);

        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals(1L, posts.get(0).getId());
        assertEquals("Content 1", posts.get(0).getContent());
        assertEquals(2L, posts.get(1).getId());
        assertEquals("Content 2", posts.get(1).getContent());
    }

    @Test
    void testToDtos() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Content 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Content 2");

        List<Post> posts = Arrays.asList(post1, post2);

        List<PostDto> postDtos = postMapper.toDtos(posts);

        assertNotNull(postDtos);
        assertEquals(2, postDtos.size());
        assertEquals(1L, postDtos.get(0).getId());
        assertEquals("Content 1", postDtos.get(0).getContent());
        assertEquals(2L, postDtos.get(1).getId());
        assertEquals("Content 2", postDtos.get(1).getContent());
    }

    @Test
    void testToEntity_WithNull() {
        PostDto postDto = null;

        Post post = postMapper.toEntity(postDto);

        assertNull(post);
    }

    @Test
    void testToDto_WithNull() {
        Post post = null;

        PostDto postDto = postMapper.toDto(post);

        assertNull(postDto);
    }

}