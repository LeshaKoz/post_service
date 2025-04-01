package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.PostViewsEvent;

public interface PostViewService {

    void addViewToPost(PostViewsEvent postViewEvent);
}
