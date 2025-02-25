package faang.school.postservice.sheduler.postcorrector.ginger;

import faang.school.postservice.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GingerCorrector {

    private final HttpClient httpClient;


    public List<Post> correct(List<Post> posts) throws IOException, InterruptedException {
        //Ginger - проверка грамматики на базе искусственного интеллекта
        for (Post post : posts) {
            String textToCorrect = post.getContent();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ginger4.p.rapidapi.com/correction?lang=US&generateRecommendations=false&flagInfomralLanguage=true"))
                    .header("x-rapidapi-key", "f08e509406msh0f69a660309f6bfp1a2c3bjsndccae6eb8803")
                    .header("x-rapidapi-host", "ginger4.p.rapidapi.com")
                    .header("Content-Type", "text/plain")
                    .header("Accept-Encoding", "identity")
                    .method("POST", HttpRequest.BodyPublishers.ofString(textToCorrect))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            post.setContent(response.body());
        }
        return posts;
    }
}
