package faang.school.postservice.client.speller;

import faang.school.postservice.dto.speller.SpellerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "yandex-speller", url = "${yandex-speller.url}")
public interface YandexSpellerClient {

    @PostMapping("/services/spellservice.json/checkText")
    List<SpellerDto> checkSpelling(@RequestParam String text);
}
