package faang.school.postservice.service.comment;


import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public boolean isCommentNotExist(long commentId) {

        log.debug("Searching for existence comment with id = {}", commentId);
        return !commentRepository.existsById(commentId);
    }
}
