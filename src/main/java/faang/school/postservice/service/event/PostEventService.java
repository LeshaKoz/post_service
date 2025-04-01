package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.PostEvent;

public interface PostEventService {

    void addPostsToFeed(PostEvent event);

}
