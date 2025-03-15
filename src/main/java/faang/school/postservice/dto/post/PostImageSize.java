package faang.school.postservice.dto.post;

import lombok.Getter;

@Getter
public enum PostImageSize {
    LARGE(1080),
    SMALL(170);

    private final int maxSideSize;

    PostImageSize(int maxSideSize) {
        this.maxSideSize = maxSideSize;
    }
}
