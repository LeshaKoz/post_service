package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@AllArgsConstructor
@Builder
@Data
public class CommentCache implements Comparable<CommentCache>, Serializable {
    private Long id;
    private String content;
    private Long authorId;

    @Override
    public int compareTo(CommentCache o) {
        return Long.compare(o.id, id);
    }
}
