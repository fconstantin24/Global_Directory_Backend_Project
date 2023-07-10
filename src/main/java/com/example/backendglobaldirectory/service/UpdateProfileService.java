package com.example.backendglobaldirectory.service;

import com.example.backendglobaldirectory.dto.ProfileDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.entities.Post;
import com.example.backendglobaldirectory.entities.PostType;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.repository.PostsRepository;
import com.example.backendglobaldirectory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UpdateProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private EmailSenderService emailSenderService;

    public ResponseEntity<?> updateProfile (ProfileDTO profileDTO, String email) throws UserNotFoundException, FileNotFoundException {

        Optional<User> userOptional = this.userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(() -> new UserNotFoundException("No user found with the given email. Can't update profile."));

        if (!Objects.equals(profileDTO.getFirstName(), user.getFirstName()) && profileDTO.getFirstName() != null) {
            user.setFirstName(profileDTO.getFirstName());
        }

        if (!Objects.equals(profileDTO.getLastName(), user.getLastName())  && profileDTO.getLastName() != null) {
            user.setLastName(profileDTO.getLastName());
        }

        if (!Objects.equals(profileDTO.getJobTitle(), user.getJobTitle())  && profileDTO.getJobTitle() != null) {
            user.setJobTitle(profileDTO.getJobTitle());

            String newJobTitle = profileDTO.getJobTitle();

            Post post = new Post(
                    PostType.PROMOTION_POST,
                    "Congratulation for promotion to " + profileDTO.getJobTitle(),
                    LocalDateTime.now(),
                    user
            );

            this.postsRepository.save(post);

            this.emailSenderService.sendPromotionEmailToUser(user, newJobTitle);
        }

        if (!Objects.equals(profileDTO.getTeam(), user.getTeam())  && profileDTO.getTeam() != null) {
            user.setTeam(profileDTO.getTeam());
        }

        if (!Objects.equals(profileDTO.getDepartment(), user.getDepartment())  && profileDTO.getDepartment() != null) {
            user.setDepartment(profileDTO.getDepartment());
        }

        if (!Objects.equals(profileDTO.getSkills(), user.getSkills()) && profileDTO.getSkills() != null) {
            user.setSkills(profileDTO.getSkills());
        }

        if (!Objects.equals(profileDTO.getPreviousExperience(), user.getPreviousExperience()) && profileDTO.getPreviousExperience() != null) {
            user.setPreviousExperience(profileDTO.getPreviousExperience());
        }

        if (!Objects.equals(profileDTO.getHobbies(), user.getHobbies()) && profileDTO.getHobbies() != null) {
            user.setHobbies(profileDTO.getHobbies());
        }

        userRepository.save(user);

        return new ResponseEntity<>(
                new ResponseDTO("Correct Token!"),
                HttpStatus.OK
        );
    }
}
