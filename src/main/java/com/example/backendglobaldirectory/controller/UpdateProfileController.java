package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.ProfileDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.service.UpdateProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@CrossOrigin
public class UpdateProfileController {

    @Autowired
    private UpdateProfileService updateProfileService;

    @PutMapping("/update")
    public ResponseEntity<?> update (@RequestBody ProfileDTO profileDTO) throws UserNotFoundException, FileNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return this.updateProfileService.updateProfile(profileDTO, email);
        }
        return new ResponseEntity<>(
                new ResponseDTO("Incorrect Token!"),
                HttpStatus.UNAUTHORIZED
        );

    }

}
