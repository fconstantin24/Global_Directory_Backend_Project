package com.example.backendglobaldirectory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseSearchDTO {
    int size;

    int numberOfPage;

    List<UserProfileDTO> userProfileDTOS;
}
