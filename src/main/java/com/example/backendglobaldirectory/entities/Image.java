package com.example.backendglobaldirectory.entities;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Schema(description = "All details about the profile image of an user.")
@NoArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(description = "The name of the image.")
    private String name;

    @Schema(description = "The type of the image, it could be: jpg, jpeg, png, etc.")
    private String type;

    @Lob
    @Column(name = "image_encoded", columnDefinition = "LONGTEXT")
    @Schema(description = "A string which represents the image encoded in Base64.")
    private String imageEncoded;

    public Image(String name, String type, String imageEncoded) {
        this.name = name;
        this.type = type;
        this.imageEncoded = imageEncoded;
    }

}
