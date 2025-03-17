package faang.school.postservice.repository;

import faang.school.postservice.model.PostHashtag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostHashtagRepository extends CrudRepository<PostHashtag, Long> {
    List<PostHashtag> findHashtagsByPostId(Long postId);
    List<PostHashtag> findByHashtagId(Long hashtagId);

    @Transactional
    void deleteByPostId(Long postId);
}
