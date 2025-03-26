package faang.school.postservice.service.feed;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsFeedService {

    private final RedisUserRepository redisUserRepository;
    private UserService userService;

    public void addAuthorToCacheByPost(Post post) {
        long authorId = post.getAuthorId();
        String username = userService.getUserDtoById(authorId).username();
        UserCache userCache = new UserCache(authorId, username);
        redisUserRepository.save(userCache);
    }
}
