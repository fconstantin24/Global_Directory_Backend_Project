package com.example.backendglobaldirectory.service;

import com.example.backendglobaldirectory.dto.CreatePostDTO;
import com.example.backendglobaldirectory.dto.ImageDTO;
import com.example.backendglobaldirectory.dto.PostDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.entities.*;
import com.example.backendglobaldirectory.exception.AccessAnotherUserResourcesException;
import com.example.backendglobaldirectory.exception.ResourceNotFoundException;
import com.example.backendglobaldirectory.repository.ImageRepository;
import com.example.backendglobaldirectory.repository.PostsRepository;
import com.example.backendglobaldirectory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.lang.module.ResolutionException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostsService {

    @Autowired
    private PostsRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private ImageRepository imageRepository;


    public void createPost(String email, CreatePostDTO createPostDTO) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Image image = ImageDTO.toImageEntity(createPostDTO.getPostImage());

            Post post = new Post(PostType.MANUAL_POST, createPostDTO.getText(), image, LocalDateTime.now(), user.get());
            postRepository.save(post);
        }
    }

    // Se executa in fiecare zi la 12:01 AM(ora Romaniei)
    @Scheduled(cron = "0 1 0 * * *", zone = "Europe/Bucharest")
    public void generateAnniversaryPosts() throws FileNotFoundException {
        List<User> users = this.userRepository.findAll();

        for(User user : users) {
            LocalDateTime dateOfEmployment = user.getDateOfEmployment();
            LocalDateTime now = LocalDateTime.now();

            if(dateOfEmployment != null && user.isActive()) {
                if(dateOfEmployment.getMonth() == now.getMonth()
                        && dateOfEmployment.getDayOfMonth() == now.getDayOfMonth()) {

                    int noOfYearsInCompany = now.getYear() - dateOfEmployment.getYear();

                    Post post = new Post(
                            PostType.ANNIVERSARY_POST,
                            "Happy " + noOfYearsInCompany + " years anniversary!",
                            LocalDateTime.now(),
                            user
                    );

                    this.postRepository.save(post);

                    this.emailSenderService.sendAnniversaryEmailToUser(user, noOfYearsInCompany);
                }
            }
        }
    }

    public void generateJoiningPost(User user) {
        Post post = new Post(
                PostType.JOINING_POST,
                "Welcome to the team, " + user.getFirstName() + " " + user.getLastName(),
                LocalDateTime.now(),
                user
        );

        this.postRepository.save(post);
    }

    public ResponseEntity<ResponseDTO> deletePostById(int id,
                                                      Principal principal)
            throws ResourceNotFoundException, AccessAnotherUserResourcesException {
        User user = this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Post post = this.postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));

        if(post.getUser().getId() != user.getId() &&
            !user.getRole().equals(Roles.ADMIN)) {
            throw new AccessAnotherUserResourcesException("You can't delete the post of " +
                    "another user if you are not admin.");
        }

        this.postRepository.delete(post);

        return new ResponseEntity<>(new ResponseDTO("Post removed successfully"),
                HttpStatus.OK);
    }
  
    public List<PostDTO> getPostsFilteredBy(Integer uid)
            throws ResourceNotFoundException {
        if(uid != null) {
            return getPostsByUserId(uid);
        }

        return getAllPosts();
    }

    private List<PostDTO> getAllPosts() {
        return PostDTO.fromEntityListToDTOList(
                this.postRepository.findAll()
        );
    }

    private List<PostDTO> getPostsByUserId(Integer uid)
            throws ResourceNotFoundException {
        User user = this.userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        return PostDTO.fromEntityListToDTOList(
                user.getPosts()
        );
    }

}
