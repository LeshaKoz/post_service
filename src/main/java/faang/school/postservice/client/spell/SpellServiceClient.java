package faang.school.postservice.client.spell;

import faang.school.postservice.dto.spell.SpellDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "spell-service", url = "${spell-service.url}")
public interface SpellServiceClient {

    @PostMapping("/checkText")
    List<SpellDto> checkText(@RequestParam("text") String text);
}
