package faang.school.postservice;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.client.UserWebClient;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import faang.school.postservice.dto.user.UserDto;
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
    private final CommentAnalyzer commentAnalyzerWebClient;
    private final UserServiceClient userServiceClient;
    private final UserWebClient userWebClient;

    @PostMapping
    public ToxicityScoreDto send() {

        UserDto user = userWebClient.getUserById(1L);
        log.info("{}",user);
        return null;
    }
}
