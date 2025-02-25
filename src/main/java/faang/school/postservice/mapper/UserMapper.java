package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import faang.school.postservice.model.cache.UserCache;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    UserCache toUserCache(UserForNewsFeedDto userForNewsFeedDto);

    UserForNewsFeedResponseDto toUserForNewsFeedResponseDto(UserDto userDto);

    UserForNewsFeedResponseDto toUserForNewsFeedResponseDto(UserCache userCache);
}
