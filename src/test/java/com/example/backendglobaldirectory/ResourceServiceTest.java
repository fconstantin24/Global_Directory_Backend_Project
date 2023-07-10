package com.example.backendglobaldirectory;

import com.example.backendglobaldirectory.dto.AddCommentDTO;
import com.example.backendglobaldirectory.dto.AddLikeDTO;
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
import com.example.backendglobaldirectory.service.ReactionsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @InjectMocks
    private ReactionsService reactionsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Principal principal;

    private User user;

    private User postOwner;

    private Post post;

    @BeforeEach
    public void initBeforeEachTest() {
        this.user = new User();
        user.setId(1);
        user.setApproved(false);
        user.setActive(false);
        user.setEmail("cinjau.costin@yahoo.com");
        user.setPassword(new BCryptPasswordEncoder().encode("05082001"));

        this.postOwner = new User();
        postOwner.setId(2);
        postOwner.setApproved(false);
        postOwner.setActive(false);
        postOwner.setEmail("cinjau.costin2@yahoo.com");
        postOwner.setPassword(new BCryptPasswordEncoder().encode("050820012"));

        this.post = new Post();
        post.setId(10);
        post.setUser(this.postOwner);
        post.setText("Mock post");
        post.setTimestamp(LocalDateTime.now());
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>());
    }

    @Test
    public void addCommentToPostFromUserWhenBothExistsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        addCommentDTO.setText("Mock comment");
        addCommentDTO.setPostId(this.post.getId());
        addCommentDTO.setUserId(this.user.getId());

        Comment comment = new Comment();
        comment.setId(100);
        comment.setText(addCommentDTO.getText());
        comment.setTimestamp(LocalDateTime.now());
        comment.setPost(this.post);
        comment.setUser(this.user);

        when(userRepository.findById(addCommentDTO.getUserId())).thenReturn(Optional.of(this.user));

        when(principal.getName()).thenReturn(this.user.getEmail());

        when(postsRepository.findById(addCommentDTO.getPostId())).thenReturn(Optional.of(this.post));

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        ResponseEntity<Comment> response = reactionsService.addCommentToPostFromUser(addCommentDTO, this.principal);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(), "The response body should contain the" +
                " new comment created");

        Comment commentFromBody = response.getBody();

        Assertions.assertEquals(this.user, commentFromBody.getUser(),
                "The comment should be associated with the user who created it.");

        Assertions.assertEquals(this.post, commentFromBody.getPost(),
                "The comment should be associated with the post who was made for.");

    }

    @Test
    public void addCommentToPostFromUserWhenUserNotExistsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        addCommentDTO.setText("Mock comment");
        addCommentDTO.setPostId(this.post.getId());
        addCommentDTO.setUserId(this.user.getId());

        when(userRepository.findById(addCommentDTO.getUserId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                this.reactionsService.addCommentToPostFromUser(addCommentDTO, principal));

        verify(commentRepository, never()).save(any(Comment.class));

        String expectedMessage = "User not found!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void addCommentToPostFromUserWhenPostNotExistsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        addCommentDTO.setText("Mock comment");
        addCommentDTO.setPostId(this.post.getId());
        addCommentDTO.setUserId(this.user.getId());

        when(userRepository.findById(addCommentDTO.getUserId())).thenReturn(Optional.of(this.user));

        when(principal.getName()).thenReturn(this.user.getEmail());

        when(postsRepository.findById(addCommentDTO.getPostId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                this.reactionsService.addCommentToPostFromUser(addCommentDTO, principal));

        verify(commentRepository, never()).save(any(Comment.class));

        String expectedMessage = "Post not found!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void addCommentToPostFromUserWhenPrincipalEmailIsNotTheOneOfUserWithUseridTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        addCommentDTO.setText("Mock comment");
        addCommentDTO.setPostId(this.post.getId());
        addCommentDTO.setUserId(this.user.getId());

        when(userRepository.findById(addCommentDTO.getUserId())).thenReturn(Optional.of(this.user));

        when(principal.getName()).thenReturn("dummy");

        Exception exception = Assertions.assertThrows(AccessAnotherUserResourcesException.class, () ->
                this.reactionsService.addCommentToPostFromUser(addCommentDTO, principal));

        verify(commentRepository, never()).save(any(Comment.class));

        String expectedMessage = "You can't add a comment with another uid than yours.";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    @DisplayName("Delete a comment when the user who created it wants to delete it.")
    public void deleteCommentWhenExistsTest() throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        Comment comment = new Comment();
        comment.setId(100);
        comment.setText("dummy comment");
        comment.setTimestamp(LocalDateTime.now());
        comment.setPost(this.post);
        comment.setUser(this.user);

        when(this.commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        when(this.principal.getName()).thenReturn(comment.getUser().getEmail());

        ResponseEntity<?> response = reactionsService.deleteCommentById(comment.getId(), principal);

        verify(this.commentRepository).delete(comment);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "The Http Status of the response should be 200 OK");

    }

    @Test
    @DisplayName("Test when the user tries to delete a bad comment id.")
    public void deleteCommentWhenNotExistsTest() throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        int commentId = 100;

        when(this.commentRepository.findById(commentId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                reactionsService.deleteCommentById(commentId, principal));

        verify(commentRepository, never()).delete(any(Comment.class));

        String expectedMessage = "Comment not found!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test when the user tries to delete a comment from it's own post.")
    public void deleteCommentWhenTheOwnerWantsItTest() throws ResourceNotFoundException,
            AccessAnotherUserResourcesException {

        Comment comment = new Comment();
        comment.setId(100);
        comment.setText("dummy comment");
        comment.setTimestamp(LocalDateTime.now());
        comment.setPost(this.post);
        comment.setUser(this.user);

        when(this.commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        when(this.principal.getName()).thenReturn(this.postOwner.getEmail());

        ResponseEntity<?> response = reactionsService.deleteCommentById(comment.getId(), principal);

        verify(this.commentRepository).delete(comment);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "The Http Status of the response should be 200 OK");
    }

    @Test
    @DisplayName("Test when a user tries to delete the comment of another user and the post is not his.")
    public void deleteCommentWhenRandomUserWantsItTest() throws ResourceNotFoundException,
            AccessAnotherUserResourcesException {
        Comment comment = new Comment();
        comment.setId(100);
        comment.setText("dummy comment");
        comment.setTimestamp(LocalDateTime.now());
        comment.setPost(this.post);
        comment.setUser(this.user);

        when(this.commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        when(this.principal.getName()).thenReturn("random_user@gmail.com");

        Exception exception = Assertions.assertThrows(AccessAnotherUserResourcesException.class, () ->
                reactionsService.deleteCommentById(comment.getId(), principal));

        verify(commentRepository, never()).delete(any(Comment.class));

        String expectedMessage = "Can't delete a comment if this or the post is not yours!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void addLikeToPostWhenIsNotAlreadyLikedTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException, DuplicateResourceException {
        AddLikeDTO addLikeDTO = new AddLikeDTO();
        addLikeDTO.setPostId(this.post.getId());
        addLikeDTO.setUserId(this.user.getId());

        Like like = new Like();
        like.setId(1000);
        like.setUser(this.user);
        like.setPost(this.post);

        when(userRepository.findById(addLikeDTO.getUserId())).thenReturn(Optional.of(this.user));

        when(principal.getName()).thenReturn(this.user.getEmail());

        when(postsRepository.findById(addLikeDTO.getPostId())).thenReturn(Optional.of(this.post));

        when(likeRepository.save(any(Like.class))).thenReturn(like);

        ResponseEntity<Like> response = reactionsService.addLikeToPostFromUser(addLikeDTO, this.principal);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(), "The response body should contain the" +
                " new like created");

        Like likeFromBody = response.getBody();

        Assertions.assertEquals(this.user, likeFromBody.getUser(),
                "The like should be associated with the user who created it.");

        Assertions.assertEquals(this.post, likeFromBody.getPost(),
                "The like should be associated with the post who was made for.");

    }

    @Test
    public void addLikeToPostWhenIsAlreadyLikedByUserTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException, DuplicateResourceException {
        AddLikeDTO addLikeDTO = new AddLikeDTO();
        addLikeDTO.setPostId(this.post.getId());
        addLikeDTO.setUserId(this.user.getId());

        Like like = new Like();
        like.setId(1000);
        like.setUser(this.user);
        like.setPost(this.post);
        post.getLikes().add(like);

        when(userRepository.findById(addLikeDTO.getUserId())).thenReturn(Optional.of(this.user));

        when(principal.getName()).thenReturn(this.user.getEmail());

        when(postsRepository.findById(addLikeDTO.getPostId())).thenReturn(Optional.of(this.post));

        Exception exception = Assertions.assertThrows(DuplicateResourceException.class, () ->
                reactionsService.addLikeToPostFromUser(addLikeDTO, principal));

        verify(likeRepository, never()).save(like);

        String expectedMessage = "User already liked post.";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void deleteLikeWhenTheUserWhoMadeItWantsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        Like like = new Like();
        like.setId(1000);
        like.setUser(this.user);
        like.setPost(this.post);

        when(this.likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        when(this.principal.getName()).thenReturn(like.getUser().getEmail());

        ResponseEntity<?> response = reactionsService.deleteLikeById(like.getId(), principal);

        verify(this.likeRepository).delete(like);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "The Http Status of the response should be 200 OK");
    }

    @Test
    public void deleteLikeWhenAnotherUserThanOneWhoGaveItWantsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        Like like = new Like();
        like.setId(1000);
        like.setUser(this.user);
        like.setPost(this.post);

        when(this.likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        when(this.principal.getName()).thenReturn("dummy@gmail.com");

        Exception exception = Assertions.assertThrows(AccessAnotherUserResourcesException.class, () ->
                reactionsService.deleteLikeById(like.getId(), principal));

        verify(likeRepository, never()).delete(like);

        String expectedMessage = "Can't delete a like if this is not yours!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteLikeWhenLikeIdNotExistsTest()
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        int likeId = 1000;

        when(this.likeRepository.findById(likeId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                reactionsService.deleteLikeById(likeId, principal));

        verify(likeRepository, never()).delete(any(Like.class));

        String expectedMessage = "Like not found!";

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

}
