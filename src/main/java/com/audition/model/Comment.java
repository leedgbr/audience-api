package com.audition.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a comment on an audition post by a user.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Comment {

    /**
     * The unique id of the comment.
     */
    private int id;

    /**
     * The name of author of the comment.
     */
    private String name;

    /**
     * The email address of the author of the comment.
     */
    private String email;

    /**
     * The text content of the comment.
     */
    private String body;

}
