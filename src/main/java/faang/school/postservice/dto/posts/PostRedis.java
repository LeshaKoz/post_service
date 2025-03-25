package faang.school.postservice.dto.posts;

import faang.school.postservice.model.*;
import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("post")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Like> likes;
    private List<Comment> comments;
    private List<Album> albums;
    private Ad ad;
    private List<Resource> resources;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TimeToLive
    @Value("${spring.data.redis.cache.post.time-to-live}")
    private Long expiration;

    public PostRedis(Post jpaEntity) {
        this.id = jpaEntity.getId();
        this.content = jpaEntity.getContent();
        this.authorId = jpaEntity.getAuthorId();
        this.projectId = jpaEntity.getProjectId();
        this.likes = jpaEntity.getLikes();
        this.comments = jpaEntity.getComments();
        this.albums = jpaEntity.getAlbums();
        this.ad = jpaEntity.getAd();
        this.resources = jpaEntity.getResources();
        this.publishedAt = jpaEntity.getPublishedAt();
        this.createdAt = jpaEntity.getCreatedAt();
        this.updatedAt = jpaEntity.getUpdatedAt();
    }
}
