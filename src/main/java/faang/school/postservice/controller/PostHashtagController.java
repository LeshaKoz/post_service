package faang.school.postservice.controller;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostHashtagService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posthashtag")
@RestController
public class PostHashtagController {
    private final PostHashtagService postHashtagService;
    private final PostMapper postMapper;

    @PostMapping("/create/{postId}")
    public ResponseEntity<Void> createHashtag(
            @PathVariable("postId") final Long postId,
            @RequestParam("hashtag") @NonNull String hashtag) {

        postHashtagService.createHashtag(postId, hashtag);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/posts-by-hashtag")
    public ResponseEntity<List<ResponsePostDto>> getPosts(
            @RequestParam (name = "hashtag") String hashtag){
        //    @RequestBody String hashtag) {

        List<Post> postsByHashtag = postHashtagService.getLimitedPostsByHashtag(hashtag, 100);
        List<ResponsePostDto> posts = postMapper.toDto(postsByHashtag);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}