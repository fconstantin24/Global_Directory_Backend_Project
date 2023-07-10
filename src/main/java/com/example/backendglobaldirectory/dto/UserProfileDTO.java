package com.example.backendglobaldirectory.dto;

import com.example.backendglobaldirectory.entities.Image;
import com.example.backendglobaldirectory.entities.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserProfileDTO {
    private int id;
    private String email;
    private boolean approved;
    private boolean active;
    private String role;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfEmployment;
    private Image profileImage;
    private List<String> skills;
    private List<String> previousExperience;
    private List<String> hobbies;
    private String team;
    private String department;
    private String jobTitle;

    public static UserProfileDTO fromUserEntity(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setApproved(user.isApproved());
        userProfileDTO.setActive(user.isActive());
        userProfileDTO.setRole(user.getRole().name());
        userProfileDTO.setFirstName(user.getFirstName());
        userProfileDTO.setLastName(user.getLastName());
        userProfileDTO.setDateOfEmployment(user.getDateOfEmployment());
        userProfileDTO.setProfileImage(user.getProfileImage());
        userProfileDTO.setSkills(user.getSkills());
        userProfileDTO.setPreviousExperience(user.getPreviousExperience());
        userProfileDTO.setHobbies(user.getHobbies());
        userProfileDTO.setTeam(user.getTeam());
        userProfileDTO.setDepartment(user.getDepartment());
        userProfileDTO.setJobTitle(user.getJobTitle());

        return userProfileDTO;
    }

    public static List<UserProfileDTO> fromUserListToUserProfileList(List<User> users) {
        List<UserProfileDTO> userProfileDTOS = new ArrayList<>();

        users.forEach(user -> {
            userProfileDTOS.add(UserProfileDTO.fromUserEntity(user));
        });

        return userProfileDTOS;
    }

}
