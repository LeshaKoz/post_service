package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.Builder;

import java.util.List;

@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record FeedItemResponseDto(

        //long postId,
        //LocalDateTime publishedAt,

        @JsonSerialize(as = PostResponseDto.class)
        @JsonDeserialize(as = PostResponseDto.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        PostResponseDto postResponseDto,
        //Post post,

        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> postLikes,
        //long postLikesCounter,

//        @JsonSetter(nulls = Nulls.SKIP)

        @JsonSetter(nulls = Nulls.SKIP)
        @JsonSerialize(contentAs = LikeDto.class)
        @JsonDeserialize(contentAs = LikeDto.class)
        List<LikeDto> commentLikes,
        long commentLikesCounter
) {
 /*   @JsonSerialize(as = Post.class)
    @JsonDeserialize(as = Post.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public record Post (
            long postId,
            String content,
            LocalDateTime publishedAt,
            long authorId
            ) {}*/

/*    public FeedItemResponseDto {
        postLikes = (postLikes != null) ? postLikes : List.of();
//        commentLikes = commentLikes != null ? commentLikes : List.of();
    }*/
}
