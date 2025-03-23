package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;

public interface LikeService {

    PostDto addLikeToPost(LikeDto likeDto);
    PostDto removeLikeFromPost(LikeDto likeDto);
    CommentDto addLikeToComment(LikeDto likeDto);
    CommentDto removeLikeFromComment(LikeDto likeDto);
}
