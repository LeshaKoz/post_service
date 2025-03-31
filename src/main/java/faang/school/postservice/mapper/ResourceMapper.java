package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "post", source = "postId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Resource toResource(ResourceDto resourceDto);

    @Mapping(target = "postId", source = "post")
    ResourceDto toResourceDto(Resource resource);

    List<Resource> toResourceList(List<ResourceDto> resourceDtoList);

    List<ResourceDto> toResourceDtoList(List<Resource> resourceList);

    default Long mapPostToId(Post post) {
        return post != null ? post.getId() : null;
    }

    default Post mapIdToPost(Long id) {
        if (id == null) {
            return null;
        }
        Post post = new Post();
        post.setId(id);
        return post;
    }
}
