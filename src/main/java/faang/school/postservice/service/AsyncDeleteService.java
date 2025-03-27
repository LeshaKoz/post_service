package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncDeleteService {

    private final AdRepository adRepository;

    @Async("threadPool")
    public void deleteExpiredBatch(List<Ad> ads) {
        adRepository.deleteAll(ads);
    }
}
