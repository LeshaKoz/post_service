package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;


public interface LikeService {

    LikePostDtoResponse createLikeForPost(LikePostDto likePostDto);

    LikeCommentDtoResponse createLikeForComment(LikeCommentDto likeCommentDto);

    void deleteLikeFromPost(long postId);

    void deleteLikeFromComment(long commentId);
}