package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.mapper.post.HashtagMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.properties.HashtagProperties;
import faang.school.postservice.repository.post.HashtagRepository;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostRepository postRepository;
    private final HashtagProperties hashtagProperties;

    @Override
    public List<HashtagResponseDto> getAllHashtags() {
        log.info("Get all hashtags");
        return hashtagRepository.findAll().stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

    @Cacheable(value = "top_hashtags")
    @Override
    public List<HashtagResponseDto> getTopHashtags() {
        log.info("Get top hashtags");
        Pageable pageable = PageRequest.of(0, hashtagProperties.getLimit());
        return hashtagRepository.getTopHashtags(pageable).stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void addHashtagToPost(HashtagRequestDto dto) {
        log.info("Start add hashtag");
        Post post = postRepository.findById(dto.postId()).orElseThrow(() -> new EntityNotFoundException(String.format("Post not found with id: %d", dto.postId())));
        Hashtag hashtag = hashtagRepository.findByName(dto.hashtag()).orElseGet(() -> buildHashtag(dto.hashtag()));
        if (!post.getHashtags().contains(hashtag)) {
            hashtagRepository.save(hashtag);
            hashtagRepository.addHashtagToPost(post.getId(), hashtag.getName());
            log.info("Hashtag added");
        }
    }

    private Hashtag buildHashtag(String hashtag) {
        return Hashtag.builder().name(hashtag).build();
    }
}
