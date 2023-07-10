package com.example.backendglobaldirectory.controller;

import com.example.backendglobaldirectory.dto.ResponseDTO;
import com.example.backendglobaldirectory.exception.*;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ResponseDTO> catchEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO> catchUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDTO> catchUsernameNotFoundException(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ThePasswordsDoNotMatchException.class)
    public ResponseEntity<ResponseDTO> catchThePasswordsDoNotMatch(ThePasswordsDoNotMatchException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ResponseDTO> catchInvalidInputException(InvalidInputException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessAnotherUserResourcesException.class)
    public ResponseEntity<ResponseDTO> catchAccessAnotherUserResourceException(AccessAnotherUserResourcesException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ResponseDTO> catchDuplicateResourceException(DuplicateResourceException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotApprovedException.class)
    public ResponseEntity<ResponseDTO> catchUserNotApprovedException(UserNotApprovedException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> catchGeneralException(Exception e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
