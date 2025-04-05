package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRedisDtoDataService {
    private final PostRepository postRepository;
    public UserRedisDto fetchUserInfo(Comment comment) {

        long authorId = comment.getAuthorId();
        String username = postRepository.getUser(authorId).username();
        String email = postRepository.getUser(authorId).email();

        return UserRedisDto.builder()
                .id(authorId)
                .username(username)
                .email(email)
                .build();
    }
}
