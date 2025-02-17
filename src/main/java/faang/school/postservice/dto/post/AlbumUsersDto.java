package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AlbumUsersDto(
        @JsonProperty("usersIds") @NotNull @NotEmpty List<Long> usersIds) {
}
