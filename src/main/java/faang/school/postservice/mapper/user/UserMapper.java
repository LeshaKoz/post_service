package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  UserCache toUserCache(UserDto dto);
}
