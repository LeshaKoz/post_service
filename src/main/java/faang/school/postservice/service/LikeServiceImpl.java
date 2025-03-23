package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    private final PostService postService;

    @Override
    public PostDto addLikeToPost(LikeDto likeDto) {
        UserDto userDto = getUserById(likeDto.userId());
        boolean isLiked = likeRepository.findByPostIdAndUserId(likeDto.postId(), likeDto.userId()).isPresent();
        if (isLiked) {
            return postMapper.toDto(postService.findPostById(likeDto.postId()));
        }
        Like like = likeMapper.toEntity(likeDto);
        Post post = postService.findPostById(likeDto.postId());
        like.setPost(post);
        likeRepository.save(like);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto removeLikeFromPost(LikeDto likeDto) {
        return null;
    }

    @Override
    public CommentDto addLikeToComment(LikeDto likeDto) {
        return null;
    }

    @Override
    public CommentDto removeLikeFromComment(LikeDto likeDto) {
        return null;
    }

    private UserDto getUserById(Long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
