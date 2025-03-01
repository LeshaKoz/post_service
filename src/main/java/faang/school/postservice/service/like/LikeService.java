package faang.school.postservice.service.like;

public interface LikeService {

    void createLikeForPost(Long postId);

    void createLikeForComment(Long commentId);

    void deleteLikeFromPost(long postId);

    void deleteLikeFromComment(long commentId);
}