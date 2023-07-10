package com.example.backendglobaldirectory.dto;

import com.example.backendglobaldirectory.entities.Comment;
import com.example.backendglobaldirectory.entities.Post;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class CommentDTO implements Comparable<CommentDTO> {

    private int id;

    private int userId;

    private int postId;

    private String userFullName;

    private String text;

    @JsonIgnore
    private long intervalInMinutes;

    private String timePassed;

    public static CommentDTO fromEntityToDTO(Comment comment) {
        User commentUser = comment.getUser();
        Post commentPost = comment.getPost();

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setUserId(commentUser.getId());
        commentDTO.setPostId(commentPost.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setUserFullName(commentUser.getFirstName() +
                " " +
                commentUser.getLastName()
        );
        commentDTO.setIntervalInMinutes(Utils.getPeriodOfTimeInMinutesFrom(comment.getTimestamp()));
        commentDTO.setTimePassed(Utils.getPeriodOfTimeAsString(commentDTO.getIntervalInMinutes()));

        return commentDTO;
    }

    public static List<CommentDTO> fromEntityListToDTOList(List<Comment> comments) {
        List<CommentDTO> commentDTOS = new ArrayList<>();

        comments.forEach(comment -> {
            commentDTOS.add(fromEntityToDTO(comment));
        });

        Collections.sort(commentDTOS);

        return commentDTOS;
    }

    @Override
    public int compareTo(CommentDTO o) {
        return Long.compare(this.intervalInMinutes, o.intervalInMinutes);
    }

}
