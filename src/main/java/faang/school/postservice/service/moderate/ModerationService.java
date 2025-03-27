package faang.school.postservice.service.moderate;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final ModerationDictionary moderationDictionary;
    private final PostRepository postRepository;
    @Getter
    @Value("${moderation.batch-size}")
    private int batchSize;

    public List<Post> getUnverifiedPostsBatch(int page) {
        Pageable pageable = PageRequest.of(page, batchSize);

        return postRepository.findUnverifiedPosts(pageable).getContent();
    }

    @Transactional
    @Async
    public void processPosts(List<Post> posts) {
        for (Post post : posts) {
            boolean containsBadWords = moderationDictionary.containsBadWords(post.getContent());
            post.setVerified(!containsBadWords);
            post.setVerifiedDate(LocalDateTime.now());
        }

        postRepository.saveAll(posts);
    }
}
