package com.audition.integration;

import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;

public class PostFixture {

    static List<AuditionPost> getExpectedPosts() {
        AuditionPost post1 = new AuditionPost();
        post1.setId(1);
        post1.setUserId(1);
        post1.setTitle("sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        post1.setBody(
            "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");

        AuditionPost post2 = new AuditionPost();
        post2.setId(2);
        post2.setUserId(1);
        post2.setTitle("qui est esse");
        post2.setBody(
            "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla");

        AuditionPost post3 = new AuditionPost();
        post3.setId(3);
        post3.setUserId(1);
        post3.setTitle("ea molestias quasi exercitationem repellat qui ipsa sit aut");
        post3.setBody(
            "et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut");

        return Arrays.asList(post1, post2, post3);
    }

    static AuditionPost getExpectedPost() {
        AuditionPost post = new AuditionPost();
        post.setId(123);
        post.setUserId(1);
        post.setTitle("sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        post.setBody(
            "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");
        return post;
    }

    static AuditionPost getExpectedPostWithComments() {
        AuditionPost post = new AuditionPost();
        post.setId(123);
        post.setUserId(1);
        post.setTitle("sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        post.setBody(
            "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");

        post.setComments(CommentFixture.getExpectedComments());
        return post;
    }
}
