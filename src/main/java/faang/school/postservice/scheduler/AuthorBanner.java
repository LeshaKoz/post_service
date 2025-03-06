package faang.school.postservice.scheduler;

import faang.school.postservice.events.UserBanEvent;
import faang.school.postservice.service.BanProducer;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthorBanner {

    private final PostService postService;
    private final BanProducer producer;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopic;

    @Value("${user-banner.posts-count-for-ban}")
    private int unverifiedPostsCountForBan;

    @Scheduled(cron = "${user-banner.cron}")
    public void banUsersWithUnverifiedPosts() {
        List<Long> banUsers = postService.getUsersForBanWithUnverifiedPosts(unverifiedPostsCountForBan);
        banUsers.stream()
                .map((id) -> new UserBanEvent(id, true))
                .forEach((event) -> producer.sendUserToBan(userBanTopic, event));
    }

}
