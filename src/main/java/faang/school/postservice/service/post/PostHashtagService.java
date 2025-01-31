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
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostHashtagService {
    //  private PostHashtegRepository postHashtegRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createHashtag(Long postId, String postHashtag) {
        Post post = getPost(postId);

        if (post.getHashtags() == null) {
            post.setHashtags(new HashSet<>());
        }
        post.getHashtags().add(postHashtag);

        postRepository.save(post);
        log.info("Hashtag '{}' added to post with id: {}", postHashtag, postId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "posts-cache", key = "#hashtag")
    public List<Post> getPostsFromCache(String hashtag) {

        log.info("Fetching posts for hashtag: {}", hashtag);
       // return postRepository.findPostIdsByHashtag(hashtag);
        List<Post> posts = postRepository.findPostIdsByHashtag(hashtag);
        log.info("Found post ids: {}", posts);
        return posts;
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}