package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void shouldConvertCommentDtoToEntityCorrectly() {
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setContent("Test content");

        Comment comment = commentMapper.toEntity(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getPostId(), comment.getPost().getId());
        assertEquals(commentDto.getContent(), comment.getContent());
        assertNull(comment.getLikes());
        assertNull(comment.getUpdatedAt());
        assertNull(comment.getLargeImageFileKey());
        assertNull(comment.getSmallImageFileKey());
    }

    @Test
    void shouldConvertCommentEntityToDtoCorrectly() {
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(1L);
        comment.setPost(post);
        comment.setContent("Test content");
        LocalDateTime createdAt = LocalDateTime.now();
        comment.setCreatedAt(createdAt);

        CommentDto commentDto = commentMapper.toDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getPost().getId(), commentDto.getPostId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
    }
}