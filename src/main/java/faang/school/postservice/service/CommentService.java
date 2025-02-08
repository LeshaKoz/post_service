package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;

    @Transactional
    public CommentReadDto addComment(CommentCreateDto commentCreateDto) {
        validateUserExists(commentCreateDto.getAuthorId());

        Post post = postService.findById(commentCreateDto.getPostId());
        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);

        return commentMapper.toReadDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentReadDto editComment(CommentUpdateDto commentUpdateDto) {
        Comment comment = getCommentById(commentUpdateDto.getCommentId());

        commentMapper.update(comment, commentUpdateDto);
        return commentMapper.toReadDto(commentRepository.save(comment));
    }

    public List<CommentReadDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId).stream().map(commentMapper::toReadDto).toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format("Комментарий с id=%d не найден", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Комментарий с id=%d не найден", commentId)));
    }

    private void validateUserExists(long authorId) {
        if (!userService.isUserExists(authorId)) {
            throw new BusinessException("Невозможно создать комментарий, т.к пользователя не существует");
        }
    }
}
