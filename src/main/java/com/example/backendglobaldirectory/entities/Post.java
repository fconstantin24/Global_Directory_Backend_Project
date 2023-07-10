package com.example.backendglobaldirectory.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Schema(description = "Details about a post, each user can have multiple " +
        "posts associated with his account.")
@NoArgsConstructor
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Each post has one of this post types: ANNIVERSARY, JOINING, PROMOTION POST")
    private PostType type;

    @Schema(description = "The details about the post, like a description.")
    private String text;

    @Schema(description = "The timestamp when it was created.")
    private LocalDateTime timestamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_image", referencedColumnName = "id")
    @JsonIgnore
    @Schema(description = "The image associated with the post.")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    @Schema(description = "The user associated with the post. Each post has only one user.")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Schema(description = "Each post can have multiple comments.")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Schema(description = "Each post can have multiple likes.")
    private List<Like> likes;

    public Post(PostType type, String text, LocalDateTime timestamp, User user) {
        this.type = type;
        this.text = text;
        this.timestamp = timestamp;
        this.user = user;
    }

    public Post(PostType type, String text, Image image, LocalDateTime timestamp, User user) {
        this.type = type;
        this.text = text;
        this.image = image;
        this.timestamp = timestamp;
        this.user = user;
    }
}
