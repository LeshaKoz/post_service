package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostHashtagService {
    //  private PostHashtegRepository postHashtegRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    @Transactional
    public void createHashtag(Long postId, String postHashtag) {
        Post post = getPost(postId);

        if (post.getHashtags() == null) {
          //  post.setHashtags(new HashSet<>());
            post.setHashtags(new ArrayList<>());
        }
        post.getHashtags().add(postHashtag);

        postRepository.save(post);
        log.info("Hashtag '{}' added to post with id: {}", postHashtag, postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getLimitedPostsByHashtag(String hashtag, int limit) {
        List<Post> allPosts = postService.getPostsByHashtag(hashtag);
        return allPosts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}












