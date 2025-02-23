package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.UploadFileException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final ImageService imageService;
    private final CommentEventPublisher publisher;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentDto) {
        validateUser(commentDto.authorId());
        Post post = getPostById(commentDto.postId());
        Comment comment = commentMapper.toCommentEntity(commentDto);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        createCommentPublisherEvent(savedComment.getPost().getAuthorId(),
                savedComment.getAuthorId(),
                savedComment.getPost().getId(),
                savedComment.getId(),
                savedComment.getCreatedAt());
        return commentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    public CommentResponseDto updateComment(long commentId, CommentUpdateDto commentUpdateDto) {
        Long authorId = userContext.getUserId();
        Comment foundComment = getById(commentId);
        if (!foundComment.getAuthorId().equals(authorId)) {
            throw new CommentValidationException(String.format("User with id %s is not allowed to update this comment.",
                    authorId));
        }
        foundComment.setContent(commentUpdateDto.content());
        return commentMapper.toCommentResponseDto(commentRepository.save(foundComment));
    }

    @Override
    public List<CommentResponseDto> getComments(CommentFiltersDto commentFiltersDto) {
        return commentRepository.findAllByPostId(commentFiltersDto.postId())
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

    @Transactional
    @Override
    public void uploadImage(Long commentId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadFileException("File is empty");
        }
        Comment foundComment = getById(commentId);
        String originalFileName = file.getOriginalFilename();
        String uniqueId = UUID.randomUUID().toString();
        String smallImageFileId = "small_" + uniqueId + "_" + originalFileName;
        String largeImageFileId = "large_" + uniqueId + "_" + originalFileName;
        foundComment.setSmallImageFileKey(smallImageFileId);
        foundComment.setLargeImageFileKey(largeImageFileId);
        commentRepository.save(foundComment);

        imageService.resizeAndUploadImage(smallImageFileId, true, file);
        imageService.resizeAndUploadImage(largeImageFileId, false, file);
    }

    private Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Comment with id %d not found", id))
                );
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Post with id %s not found.", postId))
                );
    }

    private void validateUser(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        if (user == null) {
            throw new EntityNotFoundException(String.format("User with id %s not found", authorId));
        }
    }

    private void createCommentPublisherEvent(Long postAuthorId,
                                             Long commentAuthorId,
                                             Long postId,
                                             Long commentId,
                                             LocalDateTime commentedAt) {
        CommentEvent event = new CommentEvent(postAuthorId,
                commentAuthorId,
                postId,
                commentId,
                commentedAt);
        publisher.publish(event);
    }
}
