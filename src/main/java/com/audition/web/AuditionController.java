package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsible for mapping Audition http requests and parameters to the appropriate operation on the AuditionService.
 * Http related concerns are dealt with here so that they do not pollute the Audition logic.
 */
@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    /**
     * Retrieve all posts.
     *
     * @param userId Optional user id filter.
     * @return The list of posts.
     */
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam(required = false) final String userId) {
        return auditionService.getPosts(userId);
    }

    /**
     * Retrieve single post.
     *
     * @param postId The id of the post to retrieve.
     * @return The post.
     */
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPost(@PathVariable("id") final String postId) {
        return auditionService.getPostById(postId);
    }

    /**
     * Retrieve comments for a post.
     *
     * @param postId The id of the post to retrieve comments for.
     * @return The list of comments.
     */
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Comment> getComments(@PathVariable("id") final String postId) {
        return auditionService.getComments(postId);
    }

}
