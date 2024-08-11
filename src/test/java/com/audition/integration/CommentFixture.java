package com.audition.integration;

import com.audition.model.Comment;
import java.util.Arrays;
import java.util.List;

public class CommentFixture {

    public static List<Comment> getExpectedComments() {
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setName("id labore ex et quam laborum");
        comment1.setEmail("Eliseo@gardner.biz");
        comment1.setBody(
            "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium");

        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setName("quo vero reiciendis velit similique earum");
        comment2.setEmail("Jayne_Kuhic@sydney.com");
        comment2.setBody(
            "est natus enim nihil est dolore omnis voluptatem numquam\net omnis occaecati quod ullam at\nvoluptatem error expedita pariatur\nnihil sint nostrum voluptatem reiciendis et");

        return Arrays.asList(comment1, comment2);
    }

}
