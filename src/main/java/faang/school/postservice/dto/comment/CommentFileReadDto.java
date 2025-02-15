package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class CommentFileReadDto {
    long id;
    long commentId;
    String largeImageFileKey;
    String smallImageFileKey;
}
