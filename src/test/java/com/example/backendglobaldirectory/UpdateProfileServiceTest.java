package com.example.backendglobaldirectory;

import com.example.backendglobaldirectory.dto.ProfileDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.repository.PostsRepository;
import com.example.backendglobaldirectory.repository.UserRepository;
import com.example.backendglobaldirectory.service.EmailSenderService;
import com.example.backendglobaldirectory.service.UpdateProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.FileNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateProfileServiceTest {

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @Test
    public void updateProfileOfExistingUserTest() throws UserNotFoundException, FileNotFoundException {
        String email = "miruna@gmail.com";
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFirstName("Miruna");
        profileDTO.setLastName("Constantin");
        profileDTO.setJobTitle("Intern");

        User existingUser = new User();
        existingUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> response = updateProfileService.updateProfile(profileDTO, email);

        verify(userRepository).save(existingUser);
        assertEquals("Miruna", existingUser.getFirstName());
        assertEquals("Constantin", existingUser.getLastName());
        assertEquals("Intern", existingUser.getJobTitle());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Correct Token!", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    public void updateProfileOfNotExistingUserTest() throws UserNotFoundException, FileNotFoundException {
        String email = "miruna@gmail.com";
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFirstName("Miruna");
        profileDTO.setLastName("Constantin");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            updateProfileService.updateProfile(profileDTO, email);
        });
    }
}
