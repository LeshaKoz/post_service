package faang.school.postservice.service.post.interfaces;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;
import faang.school.postservice.model.Post;

public interface PostService {
    PostDto createPostDraft(PostDto postDto);

    PostDto publishPost(PostDto postDto);

    PostDto updatePost(PostDto postDto);

    PostDto deletePost(PostDto postDto);

    PostDto getPost(PostDto postDto);

    List<PostDto> getAuthorPostDrafts(PostDto postDto);

    List<PostDto> getProjectPostDrafts(PostDto postDto);

    List<PostDto> getAuthorPublishedPosts(PostDto postDto);

    List<PostDto> getProjectPublishedPosts(PostDto postDto);

    Post getPostById(Long postId);
}
