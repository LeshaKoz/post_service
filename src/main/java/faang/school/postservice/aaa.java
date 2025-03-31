package faang.school.postservice;

import faang.school.postservice.client.CommentAnalyzerClient;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class aaa {
    private final CommentAnalyzerClient commentAnalyzerWebClient;

    @PostMapping
    public ToxicityScoreDto send() {
        return commentAnalyzerWebClient.analyzeComment("FUCK YOU! IDIOT");
    }
}
