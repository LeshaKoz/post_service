package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponseDto toDto(Post post);

}
