package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.LoginDTO;
import com.example.backendglobaldirectory.dto.LoginResponse;
import com.example.backendglobaldirectory.dto.RegisterDTO;
import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.entities.Token;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.EmailAlreadyUsedException;
import com.example.backendglobaldirectory.exception.InvalidInputException;
import com.example.backendglobaldirectory.repository.TokenRepository;
import com.example.backendglobaldirectory.service.AuthenticationService;
import com.example.backendglobaldirectory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class
AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private LogoutHandler logoutHandler;

    @Operation(summary = "Register a new account on platform",
            description = "Register an account for a new employee of company, the account will approved or rejected by an admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = User.class),
                    mediaType = "application/json")}),
            @ApiResponse(responseCode = "409",
                    description = "Bad input from the user, ex: he completed a wrong employment date",
                    content = {@Content(schema = @Schema(implementation = ResponseDTO.class),
                    mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The email completed in form is already used by another account.",
                    content = {@Content( schema = @Schema(implementation = ResponseDTO.class),
                    mediaType = "application/json")})})
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterDTO registerDTO)
            throws EmailAlreadyUsedException, InvalidInputException {
        return this.authenticationService.performRegister(registerDTO);
    }

    @Operation(summary = "Log in endpoint",
            description = "Log in on platform, it will send back an json with the userId " +
                    "and the JWT token which will be used for authentication and authorization.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = LoginResponse.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "500",
                    description = "The account is unapproved or marked as inactive by an admin, or the credentials are wrong.",
                    content = {@Content( schema = @Schema(implementation = ResponseDTO.class),
                            mediaType = "application/json")})})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO loginRequest,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        return this.authenticationService.performLogin(loginRequest, request, response);
    }

    @Operation(summary = "Log out endpoint",
            description = "Log out from platform it will send back only a http status code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = LoginResponse.class),
                            mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "The user is not logged in so he can't perform the log out.",
                    content = {@Content( schema = @Schema())})})
    @PostMapping("/logout")
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
    }

}
