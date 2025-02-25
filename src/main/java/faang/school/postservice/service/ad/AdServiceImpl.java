package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;

    @Value("${ad.delete.count}")
    private int deleteCount;

    @Transactional
    @Override
    public void deleteExpiredAds() {
        Pageable limit = PageRequest.of(0, deleteCount);
        List<Ad> expiredAds = adRepository.findExpiredAds(LocalDateTime.now(), limit).toList();
        adRepository.deleteAll(expiredAds);
    }
}
