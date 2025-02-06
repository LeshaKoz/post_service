package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;

import java.util.List;

public interface HashtagService {

    List<HashtagResponseDto> getAllHashtags();

    List<HashtagResponseDto> getTopHashtags();

    void addHashtagToPost(HashtagRequestDto dto);

}
