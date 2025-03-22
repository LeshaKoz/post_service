package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String POST_NOT_FOUND_PATTERN = "Post with ID: %s not found";
    private static final String COMMENT_NOT_FOUND_PATTERN = "Comment with ID: %s not found";

    private final PostService postService;
    private final CommentRepository commentRepository;

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                        .orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_FOUND_PATTERN));
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment createComment(Long postId, Comment comment) {
//        Post post = postService.getPost(postId).orElseThrow(
//                () -> new IllegalArgumentException(
//                        String.format(POST_NOT_FOUND_PATTERN, postId)));
        System.out.println("\n\n ----0----------------------- " + postId);
//        Optional<Post> postOpt = postService.getPost(postId);
        System.out.println("\n\n ----1----------------------- ");
//        Post post = postOpt.orElse(new Post());
        Post post = postService.getPost(postId);
        System.out.println("\n\n ----2----------------------- " + post);

        comment.setPost(post);

        System.out.println("\n\n -------------2222-------------- " + post);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String message) {
        return new Comment();
    }

    public boolean deleteComment(Long commentId) {
        return true;
    }



}
