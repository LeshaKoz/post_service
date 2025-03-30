package faang.school.postservice.dto.project;

import lombok.Builder;

@Builder
public record ProjectResponseDto(
        Long id,
        Long ownerId,
        String name,
        String description
)
{}
