package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post findPostById(long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new DataValidationException(String.format("Post with id %s not found", postId));
        }
        return post;
    }
}
