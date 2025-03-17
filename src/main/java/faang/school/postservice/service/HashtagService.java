package faang.school.postservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.PostHashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostHashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ElasticsearchClient elasticsearchClient;
    private final HashtagMapper hashtagMapper;

    public void loadAllHashtagsToRedis() {
        List<Hashtag> hashtags = (List<Hashtag>) hashtagRepository.findAll();
        for (Hashtag hashtag : hashtags) {
            String key = "hashtag:#" + hashtag.getName();
            redisTemplate.opsForHash().put(key, "updated_at", hashtag.getCreatedAt().toString());
        }
    }

    public void updateTrendingHashtags() {
        Set<String> keys = redisTemplate.keys("hashtag:#*");
        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String hashtagName = key.replace("hashtag:#", "");

            String postIds = (String) redisTemplate.opsForHash().get(key, "post_id");
            if (postIds != null) {
                long usageCount = postIds.split(",").length;
                redisTemplate.opsForZSet().add("trending_hashtags", "#" + hashtagName, usageCount);
            }
        }
    }

    public List<HashtagDto> getTrendingHashtags() {
        // This gets the top 10 (highest scores) from Redis
        Set<Object> top = redisTemplate.opsForZSet()
                .reverseRange("trending_hashtags", 0, 9);

        if (top == null) {
            return Collections.emptyList();
        }

        // Convert Set<Object> into a list of DTOs
        List<HashtagDto> result = new ArrayList<>();
        for (Object obj : top) {
            String hashtagName = obj.toString().replace("#", "");
            HashtagDto dto = new HashtagDto();
            dto.setName(hashtagName);
            result.add(dto);
        }
        return result;
    }

    public List<PostDto> getPostsByHashtag(String tag, int page, int size) {
        String redisKey = "hashtag:#" + tag;
        List<PostDto> posts = (List<PostDto>) redisTemplate.opsForHash().get(redisKey, "posts");
        if (posts != null && !posts.isEmpty()) {
            return posts;
        }

        List<PostDto> esPosts = queryElasticsearchForPosts(tag, page, size);

        if (!esPosts.isEmpty()) {
            redisTemplate.opsForHash().put(redisKey, "posts", esPosts);
        }

        return esPosts;
    }

    public List<HashtagDto> searchHashtags(String q) {
        List<HashtagDto> hashtags = new ArrayList<>();
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("hashtags_index")
                    .query(qb -> qb
                            .match(m -> m
                                    .field("name")
                                    .query(q)
                            )
                    )
                    .size(10)
            );

            SearchResponse<Hashtag> searchResponse = elasticsearchClient.search(searchRequest, Hashtag.class);

            for (Hit<Hashtag> hit : searchResponse.hits().hits()) {
                Hashtag hashtag = hit.source();
                if (hashtag != null) {
                    hashtags.add(hashtagMapper.toDto(hashtag));
                }
            }
        } catch (Exception e) {
            log.error("Error querying Elasticsearch for hashtags: {}", q, e);
        }
        return hashtags;
    }

    public void handlePostUpdate(String postContent, Long postId) {
        postHashtagRepository.deleteByPostId(postId);

        List<String> extracted = extractHashtags(postContent);
        for (String tag : extracted) {
            HashtagDto hashtagDto = createIfNotExists(tag);
            Hashtag entity = hashtagMapper.toEntity(hashtagDto);

            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPostId(postId);
            postHashtag.setHashtagId(entity.getId());
            postHashtagRepository.save(postHashtag);

            String redisKey = "hashtag:#" + tag;
            String existing = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
            String newValue = (existing == null || existing.isBlank())
                    ? String.valueOf(postId)
                    : existing + "," + postId;

            redisTemplate.opsForHash().put(redisKey, "post_id", newValue);
            redisTemplate.opsForHash().put(redisKey, "updated_at", Instant.now().toString());
            redisTemplate.opsForZSet().incrementScore("trending_hashtags", "#" + tag, 1.0);
        }
        updateTrendingHashtags();
    }

    public void handlePostDelete(Long postId) {
        List<PostHashtag> postHashtags = postHashtagRepository.findHashtagsByPostId(postId);

        for (PostHashtag postHashtag : postHashtags) {
            Long hashtagId = postHashtag.getHashtagId();
            Hashtag hashtag = hashtagRepository.findById(hashtagId).orElse(null);

            if (hashtag != null) {
                String tag = hashtag.getName();
                String redisKey = "hashtag:#" + tag;

                String postIds = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
                if (postIds != null) {
                    List<String> postIdList = new ArrayList<>(Arrays.asList(postIds.split(",")));
                    postIdList.remove(postId.toString());

                    if (postIdList.isEmpty()) {
                        redisTemplate.opsForHash().delete(redisKey, "post_id");
                    } else {
                        redisTemplate.opsForHash().put(redisKey, "post_id", String.join(",", postIdList));
                    }

                    redisTemplate.opsForZSet().incrementScore("trending_hashtags", "#" + tag, -1.0);
                }

                List<PostHashtag> remainingPostHashtags = postHashtagRepository.findByHashtagId(hashtagId);
                if (remainingPostHashtags.isEmpty()) {
                    hashtagRepository.delete(hashtag);
                }
            }
        }
        postHashtagRepository.deleteByPostId(postId);
    }

    public HashtagDto createIfNotExists(String tagName) {
        Optional<Hashtag> optional = hashtagRepository.findByName(tagName);
        if (optional.isEmpty()) {
            Hashtag newHashtag = new Hashtag();
            newHashtag.setName(tagName);
            return toDto(hashtagRepository.save(newHashtag));
        }
        return toDto(optional.get());
    }

    private HashtagDto toDto(Hashtag entity) {
        return HashtagDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }

    private List<String> extractHashtags(String content) {
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);
        List<String> hashtags = new ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group(1));
        }
        return hashtags;
    }

    private List<PostDto> queryElasticsearchForPosts(String tag, int page, int size) {
        List<PostDto> posts = new ArrayList<>();
        try {
            // Build the search request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("posts_index")
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m
                                            .term(t -> t
                                                    .field("hashtags.keyword")
                                                    .value("#" + tag)
                                            )
                                    )
                            )
                    )
                    .from(page * size)
                    .size(size)
            );

            // Execute the search request
            SearchResponse<PostDto> searchResponse = elasticsearchClient.search(searchRequest, PostDto.class);

            // Process the search hits
            for (Hit<PostDto> hit : searchResponse.hits().hits()) {
                posts.add(hit.source());
            }
        } catch (Exception e) {
            log.error("Error querying Elasticsearch for posts by hashtag: {}", tag, e);
        }
        return posts;
    }
}
