package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findAllByPostId(long postId);

    List<Comment> findAllByVerifiedIsFalse();
}
