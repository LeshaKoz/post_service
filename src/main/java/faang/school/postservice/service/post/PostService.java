package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;

import java.util.List;

public interface PostService {

    List<PostResponseDto> getPostsByHashtag(String hashtag);
}
