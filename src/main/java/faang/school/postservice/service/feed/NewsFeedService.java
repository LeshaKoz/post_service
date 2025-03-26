package faang.school.postservice.service.feed;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsFeedService {

    private final PostMapper postMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private UserService userService;

    public void addAuthorToCacheByPost(Post post) {
        long authorId = post.getAuthorId();
        String username = userService.getUserDtoById(authorId).username();
        UserCache userCache = new UserCache(authorId, username);
        redisUserRepository.save(userCache);
    }

    public void addAuthorToCacheByComment(Comment comment) {
        long authorId = comment.getId();
        String username = userService.getUserDtoById(authorId).username();

        UserCache userCache = new UserCache(authorId, username);
        redisUserRepository.save(userCache);
    }

    public void addPostToCache(Post post) {
        PostCache postCache = postMapper.toCache(post);
        redisPostRepository.save(postCache);
    }
}
