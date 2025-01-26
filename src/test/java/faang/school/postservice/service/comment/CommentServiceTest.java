package faang.school.postservice.service.comment;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.postservice.service.comment.TestData.createLike;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static faang.school.postservice.service.comment.TestData.createComment;
import static faang.school.postservice.service.comment.TestData.createCommentRequestDto;
import static faang.school.postservice.service.comment.TestData.createPost;
import static faang.school.postservice.service.comment.TestData.createUserDto;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private long authorId;
    private long commentId;
    private long postId;
    private Post post;
    private Comment comment;
    private UserDto userDto;
    private Like like1;
    private Like like2;
    private Like like3;
    private List<Like> likes;
    private List<Long> likeIds;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        authorId = 1L;
        postId = 1L;
        commentId = 1L;

        post = createPost(postId, authorId);
        userDto = createUserDto(authorId, "Author", "email");
        commentRequestDto = createCommentRequestDto("Текстовый комментарий", authorId, postId);
        comment = createComment(commentId, commentRequestDto.content(), authorId, post);
        like1 = createLike(1L, authorId, post, comment);
        like2 = createLike(2L, 2L, post, comment);
        like3 = createLike(3L, 3L, post, comment);
        likes = Arrays.asList(like1, like2, like3);
        likeIds = Arrays.asList(like1.getId(), like2.getId(), like3.getId());
        //commentResponseDto = createCommentResponseDto(commentId, commentRequestDto.content(),
        //        authorId, postId, );
    }

    @Test
    void testCreateCommentSuccess() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment savedComment = invocation.getArgument(0);
                    savedComment.setId(commentId);
                    savedComment.setLikes(likes);
                    return savedComment;
                });

        CommentResponseDto commentResponseDtoFromDb;
        commentResponseDtoFromDb = commentService.createComment(commentRequestDto);

        verifyNoMoreInteractions(userServiceClient, commentRepository, postRepository);
        verify(commentRepository, Mockito.times(1))
                .save(commentArgumentCaptor.capture());
        assertEquals(commentRequestDto.content(), commentArgumentCaptor.getValue().getContent());

        assertNotNull(commentResponseDtoFromDb);
        assertEquals(commentId, commentResponseDtoFromDb.id());
        assertEquals(commentRequestDto.authorId(), commentResponseDtoFromDb.authorId());
        assertEquals(commentRequestDto.postId(), commentResponseDtoFromDb.postId());
        assertEquals(commentRequestDto.content(), commentResponseDtoFromDb.content());
        assertEquals(likeIds, commentResponseDtoFromDb.likeIds());
    }

    @Test
    void testCreateCommentIfUserNotFoundFailed() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));
        assertEquals(String.format("User with id %s not found", authorId), exception.getMessage());
    }

    @Test
    void testCreateCommentIfPostNotFoundFailed() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));
        assertEquals(String.format("Post with id %s not found.", postId), exception.getMessage());
    }

    @Test
    void testUpdateCommentSuccess() {

    }

    @Test
    void testUpdateCommentIfUserNotAuthorFailed() {

    }

    @Test
    void testUpdateCommentIfCommentNotFoundFailed() {

    }

    @Test
    void testDeleteCommentSuccess() {

    }

    @Test
    void testDeleteCommentIfCommentNotFoundFailed() {

    }

    @Test
    void testGetCommentsByIdSuccess() {

    }
}
