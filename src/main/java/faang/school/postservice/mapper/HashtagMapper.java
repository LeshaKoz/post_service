package faang.school.postservice.mapper;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.model.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {
    Hashtag toEntity(HashtagDto hashtagDto);
    List<Hashtag> toEntity(List<HashtagDto> hashtagDto);

    HashtagDto toDto(Hashtag hashtag);
    List<HashtagDto> toDto(List<Hashtag> hashtag);

}
