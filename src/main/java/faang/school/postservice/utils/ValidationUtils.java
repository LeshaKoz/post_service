package faang.school.postservice.utils;


import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;


public class ValidationUtils {
    public static void validateLike(LikeDto likeDto) {
        if (likeDto == null ||
                likeDto.userId() == null ||
                (likeDto.commentId() == null && likeDto.postId() == null) ||
                (likeDto.postId() != null && likeDto.commentId() != null)) {
            throw new DataValidationException("Invalid data in LikeDto");
        }

    }
}
