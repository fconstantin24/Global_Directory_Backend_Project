package com.example.backendglobaldirectory.dto;

import com.example.backendglobaldirectory.entities.Comment;
import com.example.backendglobaldirectory.entities.Like;
import com.example.backendglobaldirectory.entities.Post;
import com.example.backendglobaldirectory.entities.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LikeDTO {
    private int id;
    private int userId;
    private int postId;
    private String userFullName;

    public static LikeDTO fromEntityToDTO(Like like) {
        User likeUser = like.getUser();
        Post likePost = like.getPost();

        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setId(like.getId());
        likeDTO.setPostId(likePost.getId());
        likeDTO.setUserId(likeUser.getId());
        likeDTO.setUserFullName(likeUser.getFirstName() +
                " " +
                likeUser.getLastName());

        return likeDTO;
    }

    public static List<LikeDTO> fromEntityListToDTOList(List<Like> likes) {
        List<LikeDTO> likeDTOS = new ArrayList<>();

        likes.forEach(comment -> {
            likeDTOS.add(fromEntityToDTO(comment));
        });

        return likeDTOS;
    }

}
