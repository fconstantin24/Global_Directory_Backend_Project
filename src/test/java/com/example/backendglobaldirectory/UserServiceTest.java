package com.example.backendglobaldirectory;

import com.example.backendglobaldirectory.dto.RejectDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.DuplicateResourceException;
import com.example.backendglobaldirectory.exception.UserNotApprovedException;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.repository.UserRepository;
import com.example.backendglobaldirectory.service.EmailSenderService;
import com.example.backendglobaldirectory.service.PostsService;
import com.example.backendglobaldirectory.service.TokenService;
import com.example.backendglobaldirectory.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PostsService postsService;

    private User user;

    @BeforeEach
    public void initBeforeEachTest() {
        this.user = new User();
        user.setId(1);
        user.setApproved(false);
        user.setActive(false);
        user.setEmail("cinjau.costin@yahoo.com");
    }

    @Test
    public void performAccountApproveWhenTheRegisterRequestExistsTest()
            throws UserNotFoundException, FileNotFoundException, DuplicateResourceException {
        int uid = 1;

        when(userRepository.findById(uid)).thenReturn(Optional.of(this.user));

        ResponseEntity<ResponseDTO> response = userService.performAccountApproveOrReject(uid, true, null);

        Assertions.assertTrue(this.user.isApproved(), "User should be approved.");
        Assertions.assertTrue(this.user.isActive(), "User should be active after approve.");

        verify(userRepository).save(this.user);
        verify(emailSenderService).sendApprovedNotificationEmailToUser(this.user);
        verify(postsService).generateJoiningPost(this.user);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(),
            "Body should contain a ResponseDTO");

        Assertions.assertEquals(response.getBody().getMessage(),
                "User approved succesfully.");
    }

    @Test
    public void performAccountRejectWhenTheRegisterRequestExistsTest()
            throws UserNotFoundException, FileNotFoundException, DuplicateResourceException {
        int uid = 1;

        RejectDTO rejectDTO = new RejectDTO();

        when(userRepository.findById(uid)).thenReturn(Optional.of(this.user));

        ResponseEntity<ResponseDTO> response = userService.performAccountApproveOrReject(uid, false, rejectDTO);

        verify(userRepository).deleteById(this.user.getId());
        verify(emailSenderService).sendRejectedNotificationEmailToUser(this.user, rejectDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(),
                "Body should contain a ResponseDTO");

        Assertions.assertEquals(response.getBody().getMessage(),
                "User rejected succesfully.");
    }

    @Test
    public void performAccountApproveOrRejectWhenTheRegisterRequestNotExistsTest() {
        int uid = 1;
        boolean approved = true;

        when(userRepository.findById(uid)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                this.userService.performAccountApproveOrReject(uid, approved, null));

        verify(userRepository, never()).save(any());
        verify(userRepository, never()).deleteById(anyInt());

        verify(emailSenderService, never()).sendApprovedNotificationEmailToUser(any());
        verify(emailSenderService, never()).sendRejectedNotificationEmailToUser(any(), any());

        String expectedMessage = "No user found with the given uid. " +
                "Can't perform the " + (approved ? "approve." : "reject.");

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void performAccountInactivationWhenTheAccountExistsTest()
            throws UserNotFoundException, UserNotApprovedException, DuplicateResourceException {
        int uid = 1;

        this.user.setApproved(true);
        this.user.setActive(true);

        when(userRepository.findById(uid)).thenReturn(Optional.of(this.user));

        ResponseEntity<ResponseDTO> response = userService.performAccountStatusSwitch(uid, false);

        Assertions.assertTrue(this.user.isApproved(), "User should be still approved.");
        Assertions.assertFalse(this.user.isActive(), "User should be marked as inactive.");

        verify(userRepository).save(this.user);

        verify(tokenService).revokeAllTokensForUser(uid);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(),
                "Body should contain a ResponseDTO");

        Assertions.assertEquals(response.getBody().getMessage(),
                "User inactivated succesfully.");
    }

    @Test
    public void performAccountActivationWhenTheAccountExistsTest()
            throws UserNotFoundException, UserNotApprovedException, DuplicateResourceException {
        int uid = 1;

        this.user.setApproved(true);

        when(userRepository.findById(uid)).thenReturn(Optional.of(this.user));

        ResponseEntity<ResponseDTO> response = userService.performAccountStatusSwitch(uid, true);

        Assertions.assertTrue(this.user.isApproved(), "User should be approved.");
        Assertions.assertTrue(this.user.isActive(), "User should be marked as active.");

        verify(userRepository).save(this.user);
        verify(tokenService, never()).revokeAllTokensForUser(anyInt());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "The Http Status of the response should be 200 OK");

        Assertions.assertNotNull(response.getBody(),
                "Body should contain a ResponseDTO");

        Assertions.assertEquals(response.getBody().getMessage(),
                "User activated succesfully.");
    }

    @Test
    public void performAccountActivationOrInactivationWhenNotExistsTest() {
        int uid = 1;
        boolean active = true;

        when(userRepository.findById(uid)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.performAccountStatusSwitch(uid, active);
        });

        verify(userRepository, never()).save(any());
        verify(tokenService, never()).revokeAllTokensForUser(anyInt());

        String expectedMessage = "No user found with the given uid. " +
                "Can't perform the " + (active ? "activation." : "inactivation.");

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void performAccountActivationWhereUserIsNotApproved() {
        int uid = 1;
        boolean active = true;

        when(userRepository.findById(uid)).thenReturn(Optional.of(this.user));

        Exception exception = Assertions.assertThrows(UserNotApprovedException.class, () -> {
            userService.performAccountStatusSwitch(uid, active);
        });

        Assertions.assertFalse(this.user.isActive(), "User should stay as inactive.");

        verify(userRepository, never()).save(any());
        verify(tokenService, never()).revokeAllTokensForUser(anyInt());

        String expectedMessage = "You can't activate an unapproved user.";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

}
