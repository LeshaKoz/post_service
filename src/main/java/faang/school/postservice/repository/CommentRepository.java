package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verifiedAt IS NULL")
    List<Comment> getUnverifiedComments(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM comment c WHERE c.verified_at IS NULL")
    int getUnverifiedCommentsCount();
}
