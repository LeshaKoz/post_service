package faang.school.postservice.service.post_correct.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.exception.AIIntegrationException;
import faang.school.postservice.exception.JsonNotReadException;
import faang.school.postservice.exception.PostNotCorrectedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post_correct.implementations.PostCorrectServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostCorrectServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PostCorrectServiceImpl postCorrectService;

    @Captor
    private ArgumentCaptor<TransactionCallback<Post>> captor;

    private ExecutorService executor;
    private String originalContent;
    private Post post;
    private String jsonRequest;
    private String jsonResponse;

    @BeforeEach
    void setUp() {
        postCorrectService = new PostCorrectServiceImpl(postRepository, transactionTemplate, objectMapper, webClient);
        executor = Executors.newFixedThreadPool(PostServiceConstants.ThreadPool.EXECUTOR_POOL_THREAD_NUMBER);
        originalContent = "Posssst";
        jsonRequest = "{\"cmd\":\"autocorrect\",\"lang\":\"en_US\",\"text\":\"Posssst\"}";
        jsonResponse = "{\"corrected\": \"Post\"}";
        post = Post.builder()
                .id(1L)
                .content(originalContent)
                .authorId(1L)
                .published(false)
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        shutdownExecutor(executor);
    }

    @Test
    void testCorrectPostSuccessfully() {
        String correctedContent = "Post";
        TransactionStatus transactionStatus = mock(TransactionStatus.class);
        when(transactionTemplate.execute(captor.capture())).thenAnswer(invocation -> {
            TransactionCallback<Post> callback = captor.getValue();
            return callback.doInTransaction(transactionStatus);
        });
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        doReturn(requestBodySpec).when(requestBodyUriSpec)
                .bodyValue(jsonRequest);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just(jsonResponse));
        JsonNode mockNode = mock(JsonNode.class);
        try {
            when(objectMapper.readTree("{\"corrected\": \"Post\"}")).thenReturn(mockNode);
            when(mockNode.path("corrected")).thenReturn(mockNode);
            when(mockNode.asText()).thenReturn("Post");
        } catch (IOException e) {
            throw new JsonNotReadException("IO Exception occurred: " + e.getMessage());
        } catch (Exception e) {
            throw new JsonNotReadException("Unexpected error in parsing to check spelling: " + e.getMessage());
        }

        CompletableFuture<Void> future = postCorrectService.correctPost(post, executor);

        assertDoesNotThrow(future::join);
        verify(transactionTemplate, times(1)).execute(captor.capture());
        verify(postRepository, times(1)).save(post);
        assertEquals(correctedContent, post.getContent());
        assertNotNull(post.getUpdatedAt());
    }

    @Test
    void testCorrectPost_failureAfterRetries() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        doReturn(requestBodySpec).when(requestBodyUriSpec)
                .bodyValue(jsonRequest);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new AIIntegrationException("Unexpected code received from the proof reader")));

        CompletableFuture<Void> future = postCorrectService.correctPost(post, executor);
        Exception exception = assertThrows(Exception.class, future::join);
        assertInstanceOf(PostNotCorrectedException.class, exception.getCause());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testCheckSpellingWithRetrySuccessfully() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        doReturn(requestBodySpec).when(requestBodyUriSpec)
                .bodyValue(jsonRequest);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));
        JsonNode mockNode = mock(JsonNode.class);
        try {
            when(objectMapper.readTree(jsonResponse)).thenReturn(mockNode);
            when(mockNode.path("corrected")).thenReturn(mockNode);
            when(mockNode.asText()).thenReturn("Post");
        } catch (IOException e) {
            throw new JsonNotReadException(
                    "IO Exception occurred while parsing response to check spelling: " + e.getMessage());
        } catch (Exception e) {
            throw new JsonNotReadException("Unexpected error in parsing to check spelling: " + e.getMessage());
        }

        CompletableFuture<String> future = postCorrectService.checkSpellingWithRetry(originalContent);

        String result = assertDoesNotThrow(future::join);
        assertEquals("Post", result);
        verify(webClient, times(1)).post();
    }

    @Test
    void testCheckSpellingWithRetry_retryOnFailure() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        doReturn(requestBodySpec).when(requestBodyUriSpec).bodyValue(jsonRequest);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new AIIntegrationException("Unexpected code received from the proofreader")));

        CompletableFuture<String> future = postCorrectService.checkSpellingWithRetry(originalContent);

        CompletionException completionException = assertThrows(CompletionException.class, future::join);
        assertEquals("reactor.core.Exceptions$RetryExhaustedException: Retries exhausted: 3/3",
                completionException.getCause().toString());
        assertEquals("Retries exhausted: 3/3", completionException.getCause().getMessage());
        verify(responseSpec, times(1)).bodyToMono(String.class);
    }

    @Test
    void testParseCorrectedContentSuccessfully() throws IOException {
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode correctedNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.path("corrected")).thenReturn(correctedNode);
        doReturn(false).when(correctedNode).isMissingNode();
        doReturn("Post").when(correctedNode).asText();

        String result = postCorrectService.parseCorrectedContent(jsonResponse, originalContent);

        assertEquals("Post", result);
    }

    @Test
    void testParseCorrectedContent_missingCorrectedNode() throws IOException {
        jsonResponse = "{}";
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode correctedNode = mock(JsonNode.class);

        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.path("corrected")).thenReturn(correctedNode);
        when(correctedNode.isMissingNode()).thenReturn(true);

        String result = postCorrectService.parseCorrectedContent(jsonResponse, originalContent);
        assertEquals(originalContent, result);
    }

    @Test
    void testParseCorrectedContent_withIoException() throws IOException {
        when(objectMapper.readTree(jsonResponse)).thenThrow(new JsonProcessingException("Invalid JSON") {
        });

        JsonNotReadException exception = assertThrows(JsonNotReadException.class,
                () -> postCorrectService.parseCorrectedContent(jsonResponse, originalContent));
        assertTrue(exception.getMessage().contains("IO Exception occurred while parsing"));
    }

    @Test
    void testParseCorrectedContent_withUnexpectedException() throws IOException {
        when(objectMapper.readTree(jsonResponse)).thenThrow(new NullPointerException("Unexpected error"));

        JsonNotReadException exception = assertThrows(JsonNotReadException.class,
                () -> postCorrectService.parseCorrectedContent(jsonResponse, originalContent));
        assertTrue(exception.getMessage().contains("Unexpected error in parsing"));
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(
                    PostServiceConstants.AwaitTermination.EXECUTOR_AWAIT_TERMINATION, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
