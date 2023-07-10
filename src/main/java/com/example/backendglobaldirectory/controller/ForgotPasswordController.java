package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.ForgotPasswordDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.dto.SendEmailDTO;
import com.example.backendglobaldirectory.exception.ThePasswordsDoNotMatchException;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.service.EmailSenderService;
import com.example.backendglobaldirectory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ForgotPasswordController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/reset")
    public ResponseEntity<ResponseDTO> performResetPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO)
            throws ThePasswordsDoNotMatchException, UserNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return this.userService.changePassword(forgotPasswordDTO, email);
        }
        return new ResponseEntity<>(
                new ResponseDTO("Incorrect Token!"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<Map<String, String>> performSendEmail(@RequestBody SendEmailDTO sendEmailDTO)
            throws UserNotFoundException, FileNotFoundException {
        Map<String, String> response = this.emailSenderService.createEmail(sendEmailDTO.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
