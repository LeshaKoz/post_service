package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    public List<Long> findExpiredAds() {
        LocalDateTime now = LocalDateTime.now();
        Iterable<Ad> allAds = adRepository.findAll();
        List<Long> expiredAdsIds = new ArrayList<>();
        for (Ad ad : allAds) {
            if (ad.getEndDate().isBefore(now) || ad.getAppearancesLeft() <= 0) {
                expiredAdsIds.add(ad.getId());
            }
        }
        return expiredAdsIds;
    }

    @Override
    public List<Long> deleteExpiredAds() {
        List<Long> expiredAdsIds = findExpiredAds();
        adRepository.deleteAllById(expiredAdsIds);
        return expiredAdsIds;
    }

    @Override
    public void deleteAds(List<Long> adsIds) {
        adRepository.deleteAllById(adsIds);
    }
}