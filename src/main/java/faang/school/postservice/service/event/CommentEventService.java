package faang.school.postservice.service.event;

import faang.school.postservice.dto.event.CommentEvent;

public interface CommentEventService {

    void addCommentToPostToFeed(CommentEvent commentEvent);
}
