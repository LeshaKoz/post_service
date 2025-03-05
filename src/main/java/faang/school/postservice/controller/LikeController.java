package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/like")
@Slf4j
@Validated
public class LikeController {
    private LikeService likeServiceV2;
    private LikeMapper likeMapper;
    private PostMapper postMapper;
    private UserContext userContext;

    @PostMapping("/post/{postId}/like")
    @ResponseBody
    public LikeDto likePost(@Validated @RequestBody LikeDto likeDto) {
        likeDto.setUserId(userContext.getUserId());
        Like like = likeServiceV2.likePost(likeMapper.toEntity(likeDto));
        return likeMapper.toDto(like);
    }

    @PostMapping("/post/{postId}/dislike")
    @ResponseBody
    public LikeDto dislikePost(@Validated @RequestBody LikeDto likeDto) {
        likeDto.setUserId(userContext.getUserId());
        Like like = likeServiceV2.dislikePost(likeMapper.toEntity(likeDto));
        return likeMapper.toDto(like);
    }

    @PostMapping("/post/{postId}/comment/{commentId}/like")
    @ResponseBody
    public LikeDto likeComment(@Validated @RequestBody LikeDto likeDto) {
        likeDto.setUserId(userContext.getUserId());
        Like like = likeServiceV2.likeComment(likeMapper.toEntity(likeDto));
        return likeMapper.toDto(like);
    }

    @PostMapping("/post/{postId}/comment/{commentId}/dislike")
    @ResponseBody
    public LikeDto dislikeComment(@Validated @RequestBody LikeDto likeDto) {
        likeDto.setUserId(userContext.getUserId());
        Like like = likeServiceV2.dislikeComment(likeMapper.toEntity(likeDto));
        return likeMapper.toDto(like);
    }

    @GetMapping("/post/{postId}/likes")
    @ResponseBody
    public PostDto getNumberOfPostLikes(@PathVariable("postId") @Min(value = 1, message = "postId must be greater than 0") Long postId)
    {
        return postMapper.toDto(likeServiceV2.getNumberOfPostLikes(postId));
    }
}
