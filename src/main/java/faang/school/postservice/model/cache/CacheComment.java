package faang.school.postservice.model.cache;

import lombok.Data;

@Data
public class CacheComment {
    public static final String COMMENT_PREFIX = "post_comments:";

    private Long id;
    private int likes;
    private String content;
    private String authorId;
}
