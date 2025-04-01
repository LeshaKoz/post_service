package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.HeatTask;

public interface FeedHeaterService {

    void startHeat();

    void cacheHeat(HeatTask heatTask);
}
