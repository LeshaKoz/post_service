package faang.school.postservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.util.ObjectBuilder;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.PostHashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostHashtagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private SearchResponse<Hashtag> searchResponseHashtag;

    @Mock
    private SearchResponse<PostDto> searchResponsePost;

    @Mock
    private HitsMetadata<Hashtag> hitsMetadataHashtag;

    @Mock
    private HitsMetadata<PostDto> hitsMetadataPost;

    @Mock
    private BulkResponse bulkResponse;

    @Mock
    private HashtagMapper hashtagMapper;

    @InjectMocks
    private HashtagService hashtagService;

    @Test
    void loadAllHashtagsToRedis_shouldLoadAllHashtags() {
        Hashtag hashtag1 = new Hashtag();
        hashtag1.setId(1L);
        hashtag1.setName("java");

        Hashtag hashtag2 = new Hashtag();
        hashtag2.setId(2L);
        hashtag2.setName("spring");

        List<Hashtag> hashtags = Arrays.asList(hashtag1, hashtag2);

        List<PostHashtag> postHashtags1 = Arrays.asList(
                createPostHashtag(1L, 101L),
                createPostHashtag(1L, 102L)
        );

        List<PostHashtag> postHashtags2 = List.of(
                createPostHashtag(2L, 103L)
        );

        when(hashtagRepository.findAll()).thenReturn(hashtags);
        when(postHashtagRepository.findByHashtagId(1L)).thenReturn(postHashtags1);
        when(postHashtagRepository.findByHashtagId(2L)).thenReturn(postHashtags2);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        hashtagService.loadAllHashtagsToRedis();

        verify(hashOperations).put("hashtag:#java", "post_id", "101,102");
        verify(hashOperations).put("hashtag:#spring", "post_id", "103");
        verify(hashOperations, times(2)).put(anyString(), eq("updated_at"), anyString());
        verify(zSetOperations, times(2)).incrementScore(eq("trending_hashtags"), anyString(), eq(1.0));
    }

    @Test
    void updateTrendingHashtags_shouldUpdateScores() {
        Set<String> keys = new HashSet<>(Arrays.asList("hashtag:#java", "hashtag:#spring"));
        when(redisTemplate.keys("hashtag:#*")).thenReturn(keys);
        when(hashOperations.get("hashtag:#java", "post_id")).thenReturn("101,102");
        when(hashOperations.get("hashtag:#spring", "post_id")).thenReturn("103");
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        hashtagService.updateTrendingHashtags();

        verify(zSetOperations).add("trending_hashtags", "#java", 2);
        verify(zSetOperations).add("trending_hashtags", "#spring", 1);
    }

    @Test
    void getTrendingHashtags_shouldReturnTopHashtags() {
        Set<Object> trendingTags = new HashSet<>(Arrays.asList("#java", "#spring", "#redis"));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange("trending_hashtags", 0, 9)).thenReturn(trendingTags);

        List<String> result = hashtagService.getTrendingHashtags();

        assertEquals(3, result.size());
        assertTrue(result.contains("java"));
        assertTrue(result.contains("spring"));
        assertTrue(result.contains("redis"));
    }

    @Test
    void getPostsByHashtag_shouldReturnPostsFromElasticsearch() throws IOException {
        String tag = "java";
        int page = 1;
        int size = 10;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("hashtag:#java", "post_id")).thenReturn(null);

        Hit<PostDto> hit = mock(Hit.class);
        PostDto postDto = new PostDto(101L, "sdfsf", 1L, null, LocalDateTime.now());
        when(hit.source()).thenReturn(postDto);
        List<Hit<PostDto>> hits = Collections.singletonList(hit);

        when(searchResponsePost.hits()).thenReturn(hitsMetadataPost);
        when(hitsMetadataPost.hits()).thenReturn(hits);
        doReturn(searchResponsePost).when(elasticsearchClient).search((SearchRequest) any(Function.class), eq(PostDto.class));

        List<PostDto> result = hashtagService.getPostsByHashtag(tag, page, size);

        assertFalse(result.isEmpty());
        verify(elasticsearchClient).search(any(Function.class), eq(PostDto.class));
    }

    @Test
    void searchHashtags_shouldReturnMatchingHashtags() throws IOException {
        String query = "java";

        Hashtag hashtag = new Hashtag();
        hashtag.setId(1L);
        hashtag.setName("java");

        HashtagDto hashtagDto = HashtagDto.builder().id(1L).name("java").build();

        Hit<Hashtag> hit = mock(Hit.class);
        when(hit.source()).thenReturn(hashtag);
        List<Hit<Hashtag>> hits = Collections.singletonList(hit);

        when(searchResponseHashtag.hits()).thenReturn(hitsMetadataHashtag);
        when(hitsMetadataHashtag.hits()).thenReturn(hits);
        when(elasticsearchClient.search(any(Function.class), eq(Hashtag.class))).thenReturn(searchResponseHashtag);
        when(hashtagMapper.toDto(hashtag)).thenReturn(hashtagDto);

        List<HashtagDto> result = hashtagService.searchHashtags(query);

        assertEquals(1, result.size());
        assertEquals("java", result.get(0).getName());
    }

    @Test
    void handlePostUpdate_shouldStoreHashtags() {
        String content = "Post with #java and #spring hashtags";
        Long postId = 101L;

        Hashtag javaHashtag = new Hashtag();
        javaHashtag.setId(1L);
        javaHashtag.setName("java");

        Hashtag springHashtag = new Hashtag();
        springHashtag.setId(2L);
        springHashtag.setName("spring");

        HashtagDto javaDto = HashtagDto.builder().id(1L).name("java").build();
        HashtagDto springDto = HashtagDto.builder().id(2L).name("spring").build();

        when(hashtagRepository.findByName("java")).thenReturn(Optional.of(javaHashtag));
        when(hashtagRepository.findByName("spring")).thenReturn(Optional.of(springHashtag));
        when(hashtagMapper.toDto(javaHashtag)).thenReturn(javaDto);
        when(hashtagMapper.toDto(springHashtag)).thenReturn(springDto);
        when(hashtagMapper.toEntity(javaDto)).thenReturn(javaHashtag);
        when(hashtagMapper.toEntity(springDto)).thenReturn(springHashtag);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        hashtagService.handlePostUpdate(content, postId);

        verify(postHashtagRepository).deleteByPostId(postId);
        verify(postHashtagRepository, times(2)).save(any(PostHashtag.class));
        verify(hashOperations, times(2)).put(anyString(), eq("post_id"), anyString());
        verify(hashOperations, times(2)).put(anyString(), eq("updated_at"), anyString());
    }

    @Test
    void handlePostDelete_shouldRemoveHashtagAssociations() {
        Long postId = 101L;

        PostHashtag postHashtag = new PostHashtag();
        postHashtag.setPostId(postId);
        postHashtag.setHashtagId(1L);

        Hashtag hashtag = new Hashtag();
        hashtag.setId(1L);
        hashtag.setName("java");

        when(postHashtagRepository.findHashtagsByPostId(postId)).thenReturn(Collections.singletonList(postHashtag));
        lenient().when(hashtagRepository.findById(1L)).thenReturn(Collections.singletonList(hashtag));

        hashtagService.handlePostDelete(postId);

        verify(postHashtagRepository).deleteByPostId(postId);
    }

    @Test
    void createIfNotExists_shouldCreateNewHashtagWhenNotFound() {
        String tagName = "newtag";
        Hashtag hashtag = new Hashtag();
        hashtag.setId(3L);
        hashtag.setName(tagName);
        hashtag.setCreatedAt(LocalDateTime.now());

        when(hashtagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(hashtagRepository.save(any(Hashtag.class))).thenReturn(hashtag);

        HashtagDto result = hashtagService.createIfNotExists(tagName);

        //TODO
        //assertEquals(tagName, result.getName());
        verify(hashtagRepository).save(any(Hashtag.class));
    }

    @Test
    void indexHashtagsFromRedisToElasticsearch_shouldIndexAllHashtags() throws IOException {
        Set<String> keys = new HashSet<>(Arrays.asList("hashtag:#java", "hashtag:#spring"));
        when(redisTemplate.keys("hashtag:#*")).thenReturn(keys);
        when(hashOperations.get("hashtag:#java", "post_id")).thenReturn("101,102");
        when(hashOperations.get("hashtag:#java", "updated_at")).thenReturn("2023-01-01T00:00:00Z");
        when(hashOperations.get("hashtag:#spring", "post_id")).thenReturn("103");
        when(hashOperations.get("hashtag:#spring", "updated_at")).thenReturn("2023-01-01T00:00:00Z");

        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        Function<BulkRequest.Builder, ObjectBuilder<BulkRequest>> bulkRequestFunction = builder -> bulkRequestBuilder;

        when(elasticsearchClient.bulk(any(Function.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);

        hashtagService.indexHashtagsFromRedisToElasticsearch();

        verify(elasticsearchClient).bulk(any(Function.class));
    }

    @Test
    void flushDatabase_shouldFlushRedisDb() {
        when(redisTemplate.getConnectionFactory()).thenReturn(redisConnectionFactory);
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        RedisServerCommands serverCommands = mock(RedisServerCommands.class);
        when(redisConnection.serverCommands()).thenReturn(serverCommands);

        hashtagService.flushDatabase();

        verify(serverCommands).flushDb();
    }

    @Test
    void deleteHashtagKeys_shouldDeleteAllHashtagKeys() {
        Set<String> keys = new HashSet<>(Arrays.asList("hashtag:#java", "hashtag:#spring"));
        when(redisTemplate.keys("hashtag:#*")).thenReturn(keys);

       hashtagService.deleteHashtagKeys();

        verify(redisTemplate).delete(keys);
    }

    @Test
    void deleteTrendingHashtags_shouldDeleteTrendingHashtagsKey() {
        hashtagService.deleteTrendingHashtags();

        verify(redisTemplate).delete("trending_hashtags");
    }

    private PostHashtag createPostHashtag(Long hashtagId, Long postId) {
        PostHashtag postHashtag = new PostHashtag();
        postHashtag.setHashtagId(hashtagId);
        postHashtag.setPostId(postId);
        return postHashtag;
    }
}