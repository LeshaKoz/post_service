package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedItemDto(
        long postId
/*
        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> postLikes,
        //long postLikesCounter,

        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> commentLikes,
        long commentLikesCounter*/
) {
}
