package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    public List<Ad> findExpiredAds() {
        LocalDateTime now = LocalDateTime.now();
        Iterable<Ad> allAds = adRepository.findAll();
        return StreamSupport.stream(allAds.spliterator(), true)
                .filter(ad -> ad.getEndDate().isBefore(now) || ad.getAppearancesLeft() <= 0)
                .toList();
    }

    @Override
    public void deleteAds(List<Ad> ads) {
        List<Long> ids = ads.stream()
                .map(Ad::getId)
                .toList();
        adRepository.deleteAllById(ids);
    }
}