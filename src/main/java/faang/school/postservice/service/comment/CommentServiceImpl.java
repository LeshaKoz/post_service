package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentDto) {
        validateUser(commentDto);
        Post post = getPostById(commentDto.postId());
        Comment comment = commentMapper.toCommentEntity(commentDto);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        return commentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto updateComment(long commentId, CommentRequestDto commentRequestDto) {
        Comment foundComment = getById(commentId);
        if (foundComment.getAuthorId().equals(commentRequestDto.authorId())) {
            throw new IllegalArgumentException(String.format("User with id %s is not allowed to update this comment.",
                    commentRequestDto.authorId()));
        }
        foundComment.setContent(commentRequestDto.content());
        foundComment.setUpdatedAt(LocalDateTime.now());
        return commentMapper.toCommentResponseDto(commentRepository.save(foundComment));
    }

    @Override
    public List<CommentResponseDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentResponseDto)
                .toList();
    }

    @Override
    public void deleteComment(long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Comment with id %d not found", id))
                );
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()
                        -> new IllegalArgumentException(String.format("Post with id %s not found.", postId))
                );
    }

    private void validateUser(CommentRequestDto commentDto) {
        UserDto user = userServiceClient.getUser(commentDto.authorId());
        if (user == null) {
            throw new IllegalArgumentException(String.format("User with id %s not found", commentDto.authorId()));
        }
    }
}
