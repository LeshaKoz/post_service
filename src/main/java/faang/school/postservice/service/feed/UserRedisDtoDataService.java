package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRedisDtoDataService {
    private final UserServiceClient userServiceClient;
    public UserRedisDto fetchUserInfo(Comment comment) {

        long authorId = comment.getAuthorId();
        String username = userServiceClient.getUser(authorId).username();
        String email = userServiceClient.getUser(authorId).email();
        String content = comment.getContent();

        return UserRedisDto.builder()
                .id(authorId)
                .username(username)
                .email(email)
                .content(content)
                .build();
    }
}
