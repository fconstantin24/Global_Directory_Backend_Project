package com.example.backendglobaldirectory.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Schema(description = "The details about a comment, the text of it, when it was posted, who posted it and for what post.")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String text;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    @Schema(description = "A comment can be associated to only one post.")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Schema(description = "A comment can be associated to only one user.")
    private User user;

}
