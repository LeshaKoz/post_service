package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Like;
import faang.school.postservice.model.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

class CommentMapperTest {
    private final CommentMapper commentMapper = new CommentMapperImpl();

    @Test
    @DisplayName("тест метода toCommentDto")
    void testToCommentDto() {
        CommentDto inspectedCommentDto = CommentDto.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .postId(1L)
                .build();
        Comment comment = Comment.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .post(Post.builder().id(1L).build())
                .likes(List.of(Like.builder().id(1L).build(), Like.builder().id(2L).build()))
                .build();
        assert inspectedCommentDto.equals(commentMapper.toCommentDto(comment));
    }

    @Test
    @DisplayName("Тест метода toResponseDto")
    void testToResponseDto() {
        Comment comment = Comment.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .post(Post.builder().id(1L).build())
                .createdAt(LocalDateTime.of(20, 8, 14, 12, 11))
                .updatedAt(LocalDateTime.of(20, 10, 4, 12, 11))
                .likes(List.of(Like.builder().id(1L).build(), Like.builder().id(2L).build()))
                .build();
        ResponseCommentDto inspectedResponseDto = ResponseCommentDto.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .postId(1L)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeIds(List.of(1L, 2L))
                .build();
        assert inspectedResponseDto.equals(commentMapper.toResponseDto(comment));
    }

    @Test
    @DisplayName("Тест метода toEntity")
    void testToEntity() {
        CommentDto inspectedCommentDto = CommentDto.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .postId(1L)
                .build();
        Comment comment = Comment.builder()
                .id(2L)
                .content("content")
                .authorId(2L)
                .build();
        assert comment.equals(commentMapper.toEntity(inspectedCommentDto));
    }
}
