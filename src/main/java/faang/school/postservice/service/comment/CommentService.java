package faang.school.postservice.service.comment;

import reactor.core.publisher.Mono;

public interface CommentService {
    public Mono<Void> moderateComments();
}
