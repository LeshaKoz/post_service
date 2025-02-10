package faang.school.postservice.dto.filter;

public record PostFilterDto(
        Long authorId,
        Long projectId,
        boolean isPublished
) {
}
