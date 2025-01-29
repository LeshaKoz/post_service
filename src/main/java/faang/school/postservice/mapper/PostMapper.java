package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.utils.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    String DATE_FORMAT = Constants.DATE_FORMAT;

    Post toPostEntity(PostCreateRequestDto postCreateRequestDto);

    Post toPostEntity(PostUpdateRequestDto postUpdateRequestDto);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = DATE_FORMAT)
    @Mapping(source = "publishedAt", target = "publishedAt", dateFormat = DATE_FORMAT)
    @Mapping(source = "isPublished", target = "published")
    Post toPostEntity(PostResponseDto postResponseDto);

    PostCreateRequestDto toPostRequestDto(Post post);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = DATE_FORMAT)
    @Mapping(source = "publishedAt", target = "publishedAt", dateFormat = DATE_FORMAT)
    @Mapping(source = "published", target = "isPublished")
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtos(List<Post> posts);

}
