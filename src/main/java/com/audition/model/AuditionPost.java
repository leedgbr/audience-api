package com.audition.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an audition post from a user.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AuditionPost {

    /**
     * The unique user id of the poster.
     */
    private int userId;

    /**
     * The unique id of the post.
     */
    private int id;

    /**
     * The post title.
     */
    private String title;

    /**
     * The text of the post.
     */
    private String body;

    /**
     * Any comments by other users, relating to the post.
     */
    private List<Comment> comments;

}
