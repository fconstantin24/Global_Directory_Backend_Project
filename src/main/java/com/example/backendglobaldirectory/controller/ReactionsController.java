package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.*;
import com.example.backendglobaldirectory.entities.Comment;
import com.example.backendglobaldirectory.entities.Like;
import com.example.backendglobaldirectory.exception.AccessAnotherUserResourcesException;
import com.example.backendglobaldirectory.exception.DuplicateResourceException;
import com.example.backendglobaldirectory.exception.ResourceNotFoundException;
import com.example.backendglobaldirectory.service.ReactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reactions")
@CrossOrigin
public class ReactionsController {

    @Autowired
    private ReactionsService reactionsService;

    @GetMapping("/comments")
    public List<CommentDTO> getComments(@RequestParam(required = false) Integer pid,
                                        @RequestParam(required = false) Integer uid,
                                        Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.reactionsService.getCommentsFilteredBy(pid, uid, principal);
    }

    @GetMapping("/count")
    public ResponseEntity<PostStatsDTO> countStatsForPost(@RequestParam Integer pid,
                                                          @RequestParam Integer uid)
            throws ResourceNotFoundException {
        return this.reactionsService.countStatsForPost(pid, uid);
    }

    @GetMapping("/likes")
    public List<LikeDTO> getLikes(@RequestParam(required = false) Integer pid,
                                  @RequestParam(required = false) Integer uid,
                                  Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.reactionsService.getLikesFilteredBy(pid, uid, principal);
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> addCommentToPostFromUser(
            @RequestBody AddCommentDTO addCommentDTO,
            Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.reactionsService.addCommentToPostFromUser(addCommentDTO, principal);
    }

    @PostMapping("/likes")
    public ResponseEntity<Like> addLikeToPostFromUser(
            @RequestBody AddLikeDTO addLikeDTO,
            Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException, DuplicateResourceException {
        return this.reactionsService.addLikeToPostFromUser(addLikeDTO, principal);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable int id, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.reactionsService.deleteCommentById(id, principal);
    }

    @DeleteMapping("/likes/{id}")
    public ResponseEntity<?> deleteLikeById(@PathVariable int id, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.reactionsService.deleteLikeById(id, principal);
    }

}
