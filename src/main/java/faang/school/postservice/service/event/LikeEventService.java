package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.LikeEvent;

public interface LikeEventService {

    void addLikeToPost(LikeEvent likeEvent);
}
