package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {

    @Mapping(target = "postsIds", expression = "java(mapPostIds(hashtag.getPosts()))")
    HashtagResponseDto toDto(Hashtag hashtag);

    default List<Long> mapPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
