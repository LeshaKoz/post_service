package faang.school.postservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostHashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostHashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ElasticsearchClient elasticsearchClient;
    private final HashtagMapper hashtagMapper;
    private final PostMapper postMapper;

    public void loadAllHashtagsToRedis() {
        Iterable<Hashtag> hashtags = hashtagRepository.findAll();

        for (Hashtag hashtag : hashtags) {
            String tag = hashtag.getName();
            Long tagId = hashtag.getId();
            String redisKey = "hashtag:#" + tag;

            //String existing = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
            //Iterable<PostHashtag> postsHashtags = postHashtagRepository.findByHashtagId(tagId);
            String newValue = postHashtagRepository.findByHashtagId(tagId)
                    .stream()
                    .map(postHashtag -> postHashtag.getPostId().toString())
                    .collect(Collectors.joining(","));

            redisTemplate.opsForHash().put(redisKey, "post_id", newValue);
            redisTemplate.opsForHash().put(redisKey, "updated_at", Instant.now().toString());
            redisTemplate.opsForZSet().incrementScore("trending_hashtags", "#" + tag, 1.0);
        }

        updateTrendingHashtags();
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

    public List<String> getTrendingHashtags() {
        Set<Object> top = redisTemplate.opsForZSet()
                .reverseRange("trending_hashtags", 0, 9);

        if (top == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (Object obj : top) {
            String hashtagName = obj.toString().replace("#", "");
            result.add(hashtagName);
        }
        return result;
    }

    public List<PostDto> getPostsByHashtagFromRedis(String tag, int page, int size) {
        String redisKey = "hashtag:#" + tag;
        String postIdsStr = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
        if (postIdsStr != null && !postIdsStr.isEmpty()) {
            return new ArrayList<>();
        }

        assert postIdsStr != null;
        List<Long> postIds = Arrays.stream(postIdsStr.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();

        List<Post> posts = (List<Post>) postRepository.findAllById(postIds);

        return postMapper.toDto(posts);
    }

    public List<PostDto> getPostsByHashtag(String tag, int page, int size) {
        String redisKey = "hashtag:#" + tag;

        List<PostDto> esPosts = queryElasticsearchForPosts(tag, page, size);

        //TODO

        if (!esPosts.isEmpty()) {
            redisTemplate.opsForHash().put(redisKey, "post_id", esPosts);
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

            if (entity == null) {
                log.error("empty entity in the list was skipped!");
                continue;
            }

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
            return hashtagMapper.toDto(hashtagRepository.save(newHashtag));
        }
        return hashtagMapper.toDto(optional.get());
    }

    public void indexHashtagsFromRedisToElasticsearch() {
        try {
            Set<String> hashtagKeys = redisTemplate.keys("hashtag:#*");
            if (hashtagKeys.isEmpty()) {
                log.info("No hashtags found in Redis to index");
                return;
            }

            BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
            for (String redisKey : hashtagKeys) {
                String tagName = redisKey.replace("hashtag:#", "");
                String postIdsStr = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
                String updatedAt = (String) redisTemplate.opsForHash().get(redisKey, "updated_at");

                if (postIdsStr != null && !postIdsStr.isEmpty()) {
                    // For each hashtag, create a document matching the index structure
                    Map<String, Object> document = new HashMap<>();
                    document.put("hashtags", "#" + tagName);
                    document.put("post_id", postIdsStr);
                    document.put("created_at", updatedAt);
                    document.put("content", "Hashtag: #" + tagName);

                    // Add to bulk request - use a unique ID combining tag and timestamp
                    String docId = "hashtag-" + tagName + "-" + System.currentTimeMillis();
                    bulkRequest.operations(op -> op
                            .index(idx -> idx
                                    .index("hashtags_index")
                                    .id(docId)
                                    .document(document)
                            )
                    );

                    log.info("Added hashtag to index: {}", tagName);
                }
            }
            BulkResponse response = elasticsearchClient.bulk(bulkRequest.build());
            if (response.errors()) {
                log.error("Errors during bulk indexing: {}",
                        response.items().stream()
                                .filter(item -> item.error() != null)
                                .map(item -> item.error().reason())
                                .collect(Collectors.joining(", ")));
            } else {
                log.info("Successfully indexed {} hashtags to Elasticsearch", hashtagKeys.size());
            }
        } catch (Exception e) {
            log.error("Error indexing hashtags from Redis to Elasticsearch", e);
        }
    }

    public void flushDatabase() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
    }

    public void deleteHashtagKeys() {
        Set<String> keys = redisTemplate.keys("hashtag:#*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void deleteTrendingHashtags() {
        redisTemplate.delete("trending_hashtags");
    }

//    public void indexPostsFromRedisToElasticsearch() {
//        Set<String> hashtagKeys = redisTemplate.keys("hashtag:#*");
//        if (hashtagKeys == null || hashtagKeys.isEmpty()) {
//            return;
//        }
//
//        Set<Long> postIds = new HashSet<>();
//
//        for (String redisKey : hashtagKeys) {
//            String postIdsStr = (String) redisTemplate.opsForHash().get(redisKey, "post_id");
//            if (postIdsStr != null && !postIdsStr.isEmpty()) {
//                Arrays.stream(postIdsStr.split(","))
//                        .map(Long::valueOf)
//                        .forEach(postIds::add);
//            }
//        }
//
//        if (!postIds.isEmpty()) {
//            bulkIndexPosts(postIds);
//        }
//    }
//
//    private void bulkIndexPosts(Set<Long> postIds) {
//        if (postIds.isEmpty()) {
//            log.info("No post IDs to index");
//            return;
//        }
//
//        try {
//            List<Post> posts = postRepository.findAllById(postIds);
//
//            if (posts.isEmpty()) {
//                log.info("No posts found to index");
//                return;
//            }
//
//            BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
//
//            for (Post post : posts) {
//                PostDto postDto = postMapper.toDto(post);
//
//                bulkRequest.operations(op -> op
//                        .index(idx -> idx
//                                .index("posts_index")
//                                .id(post.getId().toString())
//                                .document(postDto)
//                        )
//                );
//            }
//
//            BulkResponse response = elasticsearchClient.bulk(bulkRequest.build());
//
//            if (response.errors()) {
//                log.error("Errors during bulk indexing of posts: {}",
//                        response.items().stream()
//                                .filter(item -> item.error() != null)
//                                .map(item -> item.error().reason())
//                                .collect(Collectors.joining(", ")));
//            } else {
//                log.info("Successfully indexed {} posts to Elasticsearch", posts.size());
//            }
//        } catch (Exception e) {
//            log.error("Error bulk indexing posts to Elasticsearch", e);
//        }
//    }


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
