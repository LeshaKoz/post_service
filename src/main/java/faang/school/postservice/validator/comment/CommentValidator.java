package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    public void validComment(Comment actualComment, CommentDto expectedCommentDto) {
        long actualId = actualComment.getId();
        long expectedId = expectedCommentDto.getId();
        Long actualAuthorId = actualComment.getAuthorId();
        Long expectedAuthorId = expectedCommentDto.getAuthorId();
        Long actualPostId = actualComment.getPost().getId();
        long expectedPostId = expectedCommentDto.getPostId();
        String actualContent = actualComment.getContent();
        String expectedContent = expectedCommentDto.getContent();
        if (expectedId != actualId || expectedPostId != actualPostId) {
            throw new IllegalArgumentException("Данные не верны");
        }
        if (expectedContent == null || expectedContent.equals(actualContent)) {
            throw new IllegalArgumentException("Обновить комментарий не получилось");
        }
        if (!actualAuthorId.equals(expectedAuthorId)) {
            throw new IllegalArgumentException(String.format("Автора с id %d не найден", expectedAuthorId));
        }
    }

    public void validPostComments(Post post) {
        if (post.getComments() == null || post.getComments().isEmpty()) {
            throw new IllegalArgumentException("У поста еще нет комментариев");
        }
    }
}
