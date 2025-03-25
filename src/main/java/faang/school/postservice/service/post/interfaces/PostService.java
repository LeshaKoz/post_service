package faang.school.postservice.service.post.interfaces;

import faang.school.postservice.model.Post;

public interface PostService {
    Post getPost(Long postId);

    Post getPostById(Long postId);
}
