package faang.school.postservice.repository.like;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByPostIdAndUserId(long postId, long userId);

    void deleteByCommentIdAndUserId(long commentId, long userId);

    List<Like> findByPostId(long postId);

    List<Like> findByCommentId(long commentId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);
}
