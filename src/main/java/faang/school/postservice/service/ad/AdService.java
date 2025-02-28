package faang.school.postservice.service.ad;

import java.util.List;

public interface AdService {

    List<Long> findExpiredAds();

    void deleteAds(List<Long> ads);

    List<Long> deleteExpiredAds();
}