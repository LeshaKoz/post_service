package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.dto.post.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.LikeRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.kafka.KafkaMessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final KafkaMessageService kafkaMessageService;

    @Value("${kafka.like.topic}")
    public String likeTopic;

    @Override
    public LikeDto addLikeToPost(long postId) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException
                (String.format("Post with id = %d not found", postId)));
        Like like = new Like();
        like.setUserId(user.getId());
        like.setPost(post);
        likeRepository.save(like);
        kafkaMessageService.sendMessage(likeTopic, new LikeEvent(like.getId(), user.getId(), postId, LocalDateTime.now()));
        return new LikeDto(user.getId(), postId, LocalDateTime.now());
    }
}
