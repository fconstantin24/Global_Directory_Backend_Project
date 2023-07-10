package com.example.backendglobaldirectory.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDTO {
    private String firstName;

    private String lastName;

    private String jobTitle;

    private String team;

    private String department;

    private List<String> skills;

    private List<String> previousExperience;

    private List<String> hobbies;

}
