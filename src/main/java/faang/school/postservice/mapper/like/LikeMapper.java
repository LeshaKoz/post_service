package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    Like toEntity(LikeDto dto);

    @Mapping(source = "comment.id", target = "elementId")
    LikeDto toCommentDto(Like entity);

    @Mapping(source = "post.id", target = "elementId")
    LikeDto toPostDto(Like entity);
}
