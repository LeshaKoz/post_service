package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;

    @Override
    public void deleteExpiredAds() {
        Pageable limit = PageRequest.of(0, 10);
        List<Ad> expiredAds = adRepository.findExpiredAds(LocalDateTime.now(), limit).toList();
        adRepository.deleteAll(expiredAds);
    }
}
