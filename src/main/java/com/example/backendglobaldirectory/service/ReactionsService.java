package com.example.backendglobaldirectory.service;

import com.example.backendglobaldirectory.dto.*;
import com.example.backendglobaldirectory.entities.Comment;
import com.example.backendglobaldirectory.entities.Like;
import com.example.backendglobaldirectory.entities.Post;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.AccessAnotherUserResourcesException;
import com.example.backendglobaldirectory.exception.DuplicateResourceException;
import com.example.backendglobaldirectory.exception.ResourceNotFoundException;
import com.example.backendglobaldirectory.repository.CommentRepository;
import com.example.backendglobaldirectory.repository.LikeRepository;
import com.example.backendglobaldirectory.repository.PostsRepository;
import com.example.backendglobaldirectory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReactionsService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostsRepository postsRepository;

    public ResponseEntity<Comment> addCommentToPostFromUser(AddCommentDTO addCommentDTO, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        User user = this.userRepository.findById(addCommentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if(!user.getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException(
                    "You can't add a comment with another uid than yours."
            );
        }

        Post post = this.postsRepository.findById(addCommentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        Comment comment = new Comment();
        comment.setText(addCommentDTO.getText());
        comment.setTimestamp(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        Comment commentSaved = this.commentRepository.save(comment);

        return new ResponseEntity<>(commentSaved, HttpStatus.OK);
    }

    public ResponseEntity<Like> addLikeToPostFromUser(AddLikeDTO addLikeDTO, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException, DuplicateResourceException {
        User user = this.userRepository.findById(addLikeDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if(!user.getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException(
                    "You can't add a like with another uid than yours."
            );
        }

        Post post = this.postsRepository.findById(addLikeDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        boolean userLikedPost = checkIfUserLikedPost(
                addLikeDTO.getPostId(),
                addLikeDTO.getUserId()
        );

        if(userLikedPost) {
            throw new DuplicateResourceException("User already liked post.");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        Like likeSaved = this.likeRepository.save(like);

        return new ResponseEntity<>(likeSaved, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteCommentById(int commentId, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found!"));

        if(!comment.getUser().getEmail().equals(principal.getName()) &&
            !comment.getPost().getUser().getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException(
                    "Can't delete a comment if this or the post is not yours!"
            );
        }

        this.commentRepository.delete(comment);

        return new ResponseEntity<>(
                new ResponseDTO("Comment removed succesfully"),
                HttpStatus.OK
        );
    }

    public ResponseEntity<?> deleteLikeById(int likeId, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {

        Like like = this.likeRepository.findById(likeId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found!"));

        if(!like.getUser().getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException(
                    "Can't delete a like if this is not yours!"
            );
        }

        this.likeRepository.delete(like);

        return new ResponseEntity<>(
                new ResponseDTO("Like removed succesfully"),
                HttpStatus.OK
        );
    }

    public List<CommentDTO> getCommentsFilteredBy(Integer pid, Integer uid, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        if(pid != null) {
            return getCommentsByPostId(pid);
        }
        if(uid != null) {
            return getCommentsByUserId(uid, principal);
        }
        return getAllComments();
    }

    public List<LikeDTO> getLikesFilteredBy(Integer pid, Integer uid, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        if(pid != null && uid != null) {
            return getLikesByUserIdAndPostId(uid, pid);
        }

        if(pid != null) {
            return getLikesByPostId(pid);
        }
        if(uid != null) {
            return getLikesByUserId(uid, principal);
        }
        return getAllLikes();
    }

    public List<CommentDTO> getAllComments() {
        return CommentDTO.fromEntityListToDTOList(
                this.commentRepository.findAll()
        );
    }

    private List<LikeDTO> getAllLikes() {
        return LikeDTO.fromEntityListToDTOList(
                this.likeRepository.findAll()
        );
    }

    public List<CommentDTO> getCommentsByPostId(int pid)
            throws ResourceNotFoundException {
        Post post = this.postsRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        return CommentDTO.fromEntityListToDTOList(
                post.getComments()
        );
    }

    private List<LikeDTO> getLikesByPostId(int pid)
            throws ResourceNotFoundException {
        Post post = this.postsRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        return LikeDTO.fromEntityListToDTOList(
                post.getLikes()
        );
    }

    public List<CommentDTO> getCommentsByUserId(int uid, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        User user = this.userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if(!user.getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException("You can't see the comments of another user!");
        }

        return CommentDTO.fromEntityListToDTOList(
                user.getComments()
        );
    }

    private List<LikeDTO> getLikesByUserId(int uid, Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        User user = this.userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if(!user.getEmail().equals(principal.getName())) {
            throw new AccessAnotherUserResourcesException("You can't see the likes of another user!");
        }

        return LikeDTO.fromEntityListToDTOList(
                user.getLikes()
        );
    }

    public List<LikeDTO> getLikesByUserIdAndPostId(int uid, int pid)
            throws ResourceNotFoundException {
        Post post = this.postsRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        List<Like> likes = post.getLikes()
                .stream()
                .filter(like -> like.getUser().getId() == uid)
                .toList();

        return LikeDTO.fromEntityListToDTOList(likes);
    }

    public ResponseEntity<PostStatsDTO> countStatsForPost(Integer pid, Integer uid)
            throws ResourceNotFoundException {
        Post post = this.postsRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        boolean userLikedPost = checkIfUserLikedPost(pid, uid);

        PostStatsDTO stats = new PostStatsDTO();
        stats.setLiked(userLikedPost);
        stats.setPostId(post.getId());
        stats.setNrCommentaries(post.getComments().size());
        stats.setNrLikes(post.getLikes().size());

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    public boolean checkIfUserLikedPost(int pid, int uid)
            throws ResourceNotFoundException {
        Post post = this.postsRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found!"));

        return post.getLikes()
                .stream()
                .filter(like -> like.getUser().getId() == uid)
                .toList()
                .size() == 1;
    }

}
