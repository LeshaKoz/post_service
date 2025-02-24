package faang.school.postservice.controller.ad;

import faang.school.postservice.model.ad.Ad;
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

    @GetMapping("/expired/get")
    public List<Ad> getExpiredAds() {
        List<Ad> expiredAds = adService.findExpiredAds();
        log.info("Getting expired ads {}", expiredAds);
        return expiredAds;
    }

    @DeleteMapping("/expired/delete")
    public void deleteExpiredAds() {
        List<Ad> expiredAds = adService.findExpiredAds();
        log.info("Deleting expired ads {}", expiredAds);
        adService.deleteAds(expiredAds);
    }
}