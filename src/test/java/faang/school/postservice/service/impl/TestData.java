package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    static List<Post> getSomePosts()
    {
        List<Post> posts = new ArrayList<>();

        Post post1 = createPost(1L, "Post 1", 111L, 111L,true);
        posts.add(post1);
        Post post2 = createPost(2L, "Post 2", 111L, 111L,true);
        posts.add(post2);
        Post post3 = createPost(3L, "Post 3", 112L, 112L,true);
        posts.add(post3);
        Post post4 = createPost(4L, "Post 4", 112L, 112L,true);
        posts.add(post4);
        Post post5 = createPost(5L, "Post 5", 222L, null,true);
        posts.add(post5);
        Post post6 = createPost(6L, "Post 6", 222L, null,true);
        posts.add(post6);
        Post post7 = createPost(7L, "Post 7", 223L, null,true);
        posts.add(post7);
        Post post8 = createPost(8L, "Post 8", 223L, null,true);
        posts.add(post8);
        Post post9 = createPost(9L, "Post 9", null, 111L,false);
        posts.add(post9);
        Post post10 = createPost(10L, "Post 10", 222L, null,false);
        posts.add(post10);

        return posts;
    }

    private static Post createPost(Long id, String content, Long projectId, Long authorId, boolean isPublished) {
        return  Post.builder()
                .id(id)
                .content(content)
                .projectId(projectId)
                .authorId(authorId)
                .published(isPublished)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
