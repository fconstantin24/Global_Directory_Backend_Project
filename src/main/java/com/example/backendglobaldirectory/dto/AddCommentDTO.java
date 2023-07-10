package com.example.backendglobaldirectory.dto;

import lombok.Data;

@Data
public class AddCommentDTO {
    private String text;
    private int userId;
    private int postId;
}
