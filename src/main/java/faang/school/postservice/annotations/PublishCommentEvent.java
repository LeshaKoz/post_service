package faang.school.postservice.annotations;

import faang.school.postservice.events.Event;

public @interface PublishCommentEvent {
    Class<? extends Event>[] events();
}
