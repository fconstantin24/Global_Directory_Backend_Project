package com.example.backendglobaldirectory.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Schema(description = "Information about a JWT token generated for authentication and authorization process, or" +
        " for the forgot password mechanism.")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(description = "The token as a String.")
    private String token;

    @Schema(description = "It should be false when the token is still available and true when his expiration date passed.")
    private boolean expired;

    @Schema(description = "If the token can still be used for authentication/authorization or it was revoked because " +
            "it was replaced with another token or an admin inactivated the user. If it's true the token is unusable.")
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "A token can have only one user associated but an user can own multiple tokens.")
    private User user;

    public Token(String token, boolean expired, boolean revoked, User user) {
        this.token = token;
        this.expired = expired;
        this.revoked = revoked;
        this.user = user;
    }

}
