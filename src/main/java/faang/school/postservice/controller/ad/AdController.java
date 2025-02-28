package faang.school.postservice.controller.ad;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/expired")
    public List<Long> findExpiredAds() {
        log.debug("Finding expired ads");
        List<Long> expiredAds = adService.findExpiredAds();
        log.debug("Found expired ads: {}", expiredAds);
        return expiredAds;
    }

    @DeleteMapping("/expired")
    public List<Long> deleteExpiredAds() {
        log.debug("Deleting expired ads");
        List<Long> deletedAdsIds = adService.deleteExpiredAds();
        log.debug("Deleted expired ads: {}", deletedAdsIds);
        return deletedAdsIds;
    }
}