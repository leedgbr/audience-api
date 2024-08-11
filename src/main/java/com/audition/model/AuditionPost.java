package com.audition.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;
    private List<Comment> comments;

}
