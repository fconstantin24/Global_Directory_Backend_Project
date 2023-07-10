package com.example.backendglobaldirectory.dto;

import lombok.Data;

@Data
public class PostStatsDTO {
    private int postId;
    private boolean liked;
    private int nrLikes;
    private int nrCommentaries;
}
