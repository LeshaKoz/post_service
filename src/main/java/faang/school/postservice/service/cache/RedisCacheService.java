package faang.school.postservice.service.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;

    @Async(value = "caching")
    public void saveAuthorComment(CommentReadDto commentReadDto) {
        saveAuthor(commentReadDto.authorId());
    }

    @Async(value = "caching")
    public void saveAuthorPost(PostReadDto postReadDto) {
        saveAuthor(postReadDto.getAuthorId());
    }

    @Async(value = "caching")
    public void savePost(PostReadDto postReadDto) {
        redisPostRepository.save(PostCache.builder()
                .postId(postReadDto.getId())
                .authorId(postReadDto.getAuthorId())
                .content(postReadDto.getContent())
                .build());
    }

    private void saveAuthor(long authorId) {
        redisUserRepository.save(UserCache.builder()
                .userId(authorId)
                .userName(userServiceClient
                        .getUser(authorId)
                        .username())
                .build());
    }
}
