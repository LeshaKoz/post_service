package faang.school.postservice.service.post.corrector.rapid;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.sheduler.postcorrector.ginger.GingerCorrector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GingerCorrectorTest {
    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private GingerCorrector gingerCorrector;

    @Test
    void testCorrect() throws IOException, InterruptedException {
        Post post = new Post();
        post.setContent("Ths is an eror");
        List<Post> posts = List.of(post);
        HttpResponse<String> mockedResponse = mock(HttpResponse.class);

        when(mockedResponse.body()).thenReturn("This is an error");
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockedResponse);

        List<Post> result = gingerCorrector.correct(posts);

        assertEquals("This is an error", result.get(0).getContent());
        verify(httpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}
