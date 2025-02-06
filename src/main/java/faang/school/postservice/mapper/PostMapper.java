package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostDTO postDTO);
    PostDTO toDto(Post post);

    List<Post> toListEntity(List<PostDTO> listDto);
    List<PostDTO> toListDto(List<Post> listEntity);
}
