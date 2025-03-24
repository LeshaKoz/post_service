package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    public PostDto addLikeToPost(LikeDto likeDto) {
        getUserById(likeDto.userId());
        Post post = postService.findPostById(likeDto.postId());
        if (isLikedPost(likeDto.postId(), likeDto.userId())) {
            return postMapper.toDto(post);
        }
        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto removeLikeFromPost(LikeDto likeDto) {
        getUserById(likeDto.userId());
        Post post = postService.findPostById(likeDto.postId());
        if (!isLikedPost(likeDto.postId(), likeDto.userId())) {
            return postMapper.toDto(post);
        }
        likeRepository.deleteByPostIdAndUserId(likeDto.postId(), likeDto.userId());
        return postMapper.toDto(post);
    }

    @Override
    public CommentDto addLikeToComment(LikeDto likeDto) {
        getUserById(likeDto.userId());
        Comment comment = commentService.findCommentById(likeDto.commentId());
        if(isLikedComment(likeDto.commentId(), likeDto.userId())){
           return commentMapper.toDto(comment);
        }
        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto removeLikeFromComment(LikeDto likeDto) {
        getUserById(likeDto.userId());
        Comment comment = commentService.findCommentById(likeDto.commentId());
        if(!isLikedComment(likeDto.commentId(), likeDto.userId())){
            return commentMapper.toDto(comment);
        }
        likeRepository.deleteByCommentIdAndUserId(likeDto.commentId(), likeDto.userId());
        return commentMapper.toDto(comment);
    }

    private boolean isLikedPost(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isLikedComment(Long commentId, Long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId,userId).isPresent();
    }


    private void getUserById(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    }
}
