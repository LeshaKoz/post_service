package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.time.LocalDateTime;

class CommentMapperTest {

    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void testToDto() {
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test content");
        comment.setAuthorId(2L);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        CommentDto result = commentMapper.toDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        assertEquals(2L, result.getAuthorId());
        assertEquals(1L, result.getPostId());
    }

    @Test
    void testToEntity() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setContent("Test content");
        createDto.setAuthorId(2L);
        createDto.setPostId(1L);

        Comment result = commentMapper.toEntity(createDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Test content", result.getContent());
        assertEquals(2L, result.getAuthorId());
        assertNotNull(result.getPost());
        assertEquals(1L, result.getPost().getId());
        assertNull(result.getLikes());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getLargeImageFileKey());
        assertNull(result.getSmallImageFileKey());
    }

    @Test
    void testUpdateEntity() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Old content");
        comment.setAuthorId(2L);
        comment.setPost(new Post());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setId(1L);
        updateDto.setContent("Updated content");
        updateDto.setAuthorId(3L);

        commentMapper.updateEntity(updateDto, comment);

        assertNotNull(comment);
        assertEquals(1L, comment.getId());
        assertEquals("Updated content", comment.getContent());
        assertEquals(3L, comment.getAuthorId());
        assertNotNull(comment.getPost());
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
        assertNull(comment.getLikes());
        assertNull(comment.getLargeImageFileKey());
        assertNull(comment.getSmallImageFileKey());
    }
}