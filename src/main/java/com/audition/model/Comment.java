package com.audition.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Comment {

    private int id;
    private String name;
    private String email;
    private String body;

}
