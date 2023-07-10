package com.example.backendglobaldirectory.dto;

import com.example.backendglobaldirectory.entities.Post;
import lombok.Data;


@Data
public class CreatePostDTO {

    private String text;

    private ImageDTO postImage;

    public static CreatePostDTO fromUserEntity(Post post) {
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setText(post.getText());
        createPostDTO.setPostImage(ImageDTO.fromEntity(post.getImage()));

        return createPostDTO;
    }
}
