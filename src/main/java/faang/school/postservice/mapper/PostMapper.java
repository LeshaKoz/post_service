package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "content", source = "content")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "scheduledAt", source = "scheduledAt")
    Post createDtoToEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "totalLikes")
    @Mapping(source = "comments", target = "commentIds")
    @Mapping(source = "resources", target = "resourcesIds")
    @Mapping(source = "ad.id", target = "adId")
    PostViewDto toViewDto(Post post);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "content", source = "content")
    @Mapping(target = "published", source = "published")
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "scheduledAt", source = "scheduledAt")
    void update(PostUpdateDto source, @MappingTarget Post target);

    default long getTotalLikes(List<Like> likes) {
        return likes.size();
    }

    default List<Long> getCommentIds(List<Comment> comments) {
        return comments.stream().map(Comment::getId).toList();
    }

    default List<Long> getResourcesIds(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }
}
