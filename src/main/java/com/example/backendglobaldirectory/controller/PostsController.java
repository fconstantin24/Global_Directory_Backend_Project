package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.CreatePostDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.exception.AccessAnotherUserResourcesException;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.dto.PostDTO;
import com.example.backendglobaldirectory.exception.ResourceNotFoundException;

import com.example.backendglobaldirectory.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostsController {

    @Autowired
    private PostsService postsService;

    @GetMapping
    public List<PostDTO> getPosts(@RequestParam(required = false) Integer uid)
            throws ResourceNotFoundException {
        return this.postsService.getPostsFilteredBy(uid);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletePost(@PathVariable int id,
                                                  Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        return this.postsService.deletePostById(id, principal);
    }

    @PostMapping("/new")
    public ResponseEntity<ResponseDTO> createPost(@RequestBody CreatePostDTO createPostDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            postsService.createPost(email, createPostDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO("Post created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
