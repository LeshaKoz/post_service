package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.Hashtag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, JpaSpecificationExecutor<Hashtag> {

    @Query(nativeQuery = true, value = "INSERT INTO post_hashtag (post_id, hashtag_name) VALUES (:postId, :hashtagName)")
    @Modifying
    void addHashtagToPost(long postId, String hashtagName);

    Optional<Hashtag> findByName(String name);

    default List<Hashtag> getTopHashtags(Pageable pageable) {
        return findAll((Specification<Hashtag>) (root, query, criteriaBuilder) -> {
            query.groupBy(root.get("id"));
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(root.get("posts"))));
            return criteriaBuilder.conjunction();
        }, pageable).getContent();
    }
}
