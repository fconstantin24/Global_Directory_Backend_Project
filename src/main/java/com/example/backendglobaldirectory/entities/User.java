package com.example.backendglobaldirectory.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Schema(description = "All the details about an user, including his credentials and profile details.")
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    @Schema(description = "The email used for authentication and email service.")
    private String email;

    @Column(nullable = false)
    @Schema(description = "The password which will be stored in database in encrypted format using BCrypt.")
    private String password;

    @Schema(description = "If the account was approved by an admin. If it's true the user can log-in on the platform, " +
            "otherwise he will not be able to use the registered account.")
    private boolean approved;

    @Schema(description = "It's true if the user it's still an employee of the company. " +
            "If the employee lefts the company, the account will not be deleted, but inactivated by an admin, " +
            "so if he will rejoin, he will not be supposed to register another account, the old one will be reactivated.")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Schema(description = "The role of the user, it can have 2 values: USER-for employees, ADMIN-for system admins")
    private Roles role;

    @Column(name = "first_name")
    @Schema(description = "First name of the user.")
    private String firstName;

    @Column(name = "last_name")
    @Schema(description = "Last name of the user.")
    private String lastName;

    @Column(name = "date_of_employment")
    @Schema(description = "The date when he was employed by the company.")
    private LocalDateTime dateOfEmployment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image", referencedColumnName = "id")
    @Schema(description = "Each user can have only one profile image.")
    private Image profileImage;

    @Schema(description = "A list with some important skills.")
    private List<String> skills;

    @Schema(description = "Some details about each previous experience.")
    private List<String> previousExperience;

    @Schema(description = "List of hobbies.")
    private List<String> hobbies;

    @Schema(description = "The team of user.")
    private String team;

    @Schema(description = "The department of user.")
    private String department;

    @Column(name = "job_title")
    @Schema(description = "His position in company.")
    private String jobTitle;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Schema(description = "Each user can have multiple tokens: generated for auth or forgot password.")
    private List<Token> tokens;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Schema(description = "Each user can have multiple posts associated.")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Schema(description = "Each user can have multiple comments.")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Schema(description = "Each user can have multiple likes.")
    private List<Like> likes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return approved;
    }

}
