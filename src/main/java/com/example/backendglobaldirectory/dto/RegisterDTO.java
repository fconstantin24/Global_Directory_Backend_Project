package com.example.backendglobaldirectory.dto;

import com.example.backendglobaldirectory.entities.Roles;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.InvalidInputException;
import com.example.backendglobaldirectory.utils.Utils;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Data
public class RegisterDTO {
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String dateOfEmployment;

    private String jobTitle;

    private String team;

    private String department;

    private ImageDTO image;

    public static User toUserEntity(RegisterDTO registerDTO,
                                    PasswordEncoder passwordEncoder)
            throws InvalidInputException {

        LocalDateTime dateOfEmployment = Utils.convertDateStringToLocalDateTime(
                registerDTO.getDateOfEmployment()
        ).orElseThrow(() -> new InvalidInputException("Wrong date of employment."));

        if(registerDTO.getEmail() == null || registerDTO.getPassword() == null) {
            return null;
        }

        User newUser = new User();
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setFirstName(registerDTO.getFirstName());
        newUser.setLastName(registerDTO.getLastName());
        newUser.setJobTitle(registerDTO.getJobTitle());
        newUser.setTeam(registerDTO.getTeam());
        newUser.setDepartment(registerDTO.getDepartment());
        newUser.setDateOfEmployment(dateOfEmployment);
        newUser.setRole(Roles.USER);
        newUser.setApproved(false);

        return newUser;
    }

}
