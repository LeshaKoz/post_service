package faang.school.postservice.service;

import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final RedisService redisService;

    @Value("${post-service.comment.count-of-unverified-comments-for-ban}")
    private int maxUnverifiedComments;

    public void collectAndPushUsersForBan() {
        commentRepository.getUnverifiedCommentAuthorCountDto().stream()
                .filter(banAuthorByCommentsDto ->
                        banAuthorByCommentsDto.count() >= maxUnverifiedComments)
                .forEach(banAuthorByCommentsDto -> {
                            log.debug("Push user: {} to redis for ban", banAuthorByCommentsDto);
                            redisService.pushToRedisUsersForBan(banAuthorByCommentsDto.authorId());
                        }
                );
    }
}
