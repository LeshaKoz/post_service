package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/feed-heater")
public class FeedHeatController {

    private final FeedHeaterService feedHeaterService;

    @PostMapping("/heat")
    public String startHeat() {
        feedHeaterService.startHeat();
        return "Feed cache heating process started";
    }
}
