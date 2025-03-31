package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.feed.FeedItemResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {
    @Mapping(target = "postResponseDto", source = "postResponseDto")
    @Mapping(target = "postLikes", source = "feedItemDto.postLikes")
    @Mapping(target = "postLikesCounter", source = "feedItemDto.postLikesCounter")
    @Mapping(target = "commentLikes", source = "feedItemDto.commentLikes")
    @Mapping(target = "commentLikesCounter", source = "feedItemDto.commentLikesCounter")
    FeedItemResponseDto toFeedResponseDto(FeedItemDto feedItemDto, PostResponseDto postResponseDto);


   /* default FeedItemResponseDto.Post mapPost(Post post) {
        return new FeedItemResponseDto.Post(
                post.getId(),
                post.getContent(),
                post.getPublishedAt(),
                post.getAuthorId()
        );
    }*/
}
