package faang.school.postservice.client;

import faang.school.postservice.dto.rewriterai.RewriterAiRequest;
import faang.school.postservice.dto.rewriterai.RewriterAiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "rewriter-api", url = "https://tinq.ai/api/v2/rewrite")
public interface RewriterClient {

    @PostMapping
    RewriterAiResponse rewrite(
            @RequestBody RewriterAiRequest request,
            @RequestHeader("Authorization") String bearerToken);
}
