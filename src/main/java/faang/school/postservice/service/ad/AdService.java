package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface AdService {

    List<Ad> findExpiredAds();

    void deleteAds(List<Ad> ads);
}