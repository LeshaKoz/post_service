package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import faang.school.postservice.dto.like.LikeDto;
import lombok.Builder;

import java.util.List;
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record FeedItemDto(
        long postId,

        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> postLikes,
        long postLikesCounter,

        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> commentLikes,
        long commentLikesCounter
) {
}
