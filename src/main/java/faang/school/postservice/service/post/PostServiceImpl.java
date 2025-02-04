package faang.school.postservice.service.post;


import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public boolean isPostNotExist(long postId) {

        log.debug("Searching for existence post with id {}", postId);
        return !postRepository.existsById(postId);
    }
}
