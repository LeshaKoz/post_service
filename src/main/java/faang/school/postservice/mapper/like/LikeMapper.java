package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    Like dtoToEntity(LikeDto dto);
}
