package com.audition.integration;

import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;

public class PostFixture {

    static List<AuditionPost> getExpectedPosts() {
        final AuditionPost post1 = new AuditionPost();
        post1.setId(1);
        post1.setUserId(1);
        post1.setTitle("sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        post1.setBody(
            "quia et suscipit\n"
                + "suscipit recusandae consequuntur expedita et cum\n"
                + "reprehenderit molestiae ut ut quas totam\n"
                + "nostrum rerum est autem sunt rem eveniet architecto");

        final AuditionPost post2 = new AuditionPost();
        post2.setId(2);
        post2.setUserId(1);
        post2.setTitle("qui est esse");
        post2.setBody(
            "est rerum tempore vitae\n"
                + "sequi sint nihil reprehenderit dolor beatae ea dolores neque\n"
                + "fugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\n"
                + "qui aperiam non debitis possimus qui neque nisi nulla");

        final AuditionPost post3 = new AuditionPost();
        post3.setId(3);
        post3.setUserId(1);
        post3.setTitle("ea molestias quasi exercitationem repellat qui ipsa sit aut");
        post3.setBody(
            "et iusto sed quo iure\n"
                + "voluptatem occaecati omnis eligendi aut ad\n"
                + "voluptatem doloribus vel accusantium quis pariatur\n"
                + "molestiae porro eius odio et labore et velit aut");

        return Arrays.asList(post1, post2, post3);
    }

    static List<AuditionPost> getExpectedPostsForUser() {
        final AuditionPost post1 = new AuditionPost();
        post1.setId(11);
        post1.setUserId(8);
        post1.setTitle("title11");
        post1.setBody("body11");

        final AuditionPost post2 = new AuditionPost();
        post2.setId(12);
        post2.setUserId(8);
        post2.setTitle("title12");
        post2.setBody("body12");

        final AuditionPost post3 = new AuditionPost();
        post3.setId(13);
        post3.setUserId(8);
        post3.setTitle("title13");
        post3.setBody("body13");

        return Arrays.asList(post1, post2, post3);
    }

    static AuditionPost getExpectedPostWithComments() {
        final AuditionPost post = new AuditionPost();
        post.setId(123);
        post.setUserId(1);
        post.setTitle("sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
        post.setBody(
            "quia et suscipit\n"
                + "suscipit recusandae consequuntur expedita et cum\n"
                + "reprehenderit molestiae ut ut quas totam\n"
                + "nostrum rerum est autem sunt rem eveniet architecto");

        post.setComments(CommentFixture.getExpectedComments());
        return post;
    }
}
