package com.example.backendglobaldirectory.service;

import com.example.backendglobaldirectory.dto.*;
import com.example.backendglobaldirectory.entities.Roles;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.DuplicateResourceException;
import com.example.backendglobaldirectory.exception.ThePasswordsDoNotMatchException;
import com.example.backendglobaldirectory.exception.UserNotApprovedException;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PostsService postsService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    public ResponseEntity<ResponseDTO> performAccountApproveOrReject(
            int uid, boolean approved, RejectDTO rejectDTO)
            throws UserNotFoundException, DuplicateResourceException {

        User user = this.userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given uid. " +
                        "Can't perform the " + (approved ? "approve." : "reject.")));

        if (user.isApproved()) {
            throw new DuplicateResourceException("User already approved.");
        }

        if (approved) {
            user.setApproved(true);
            user.setActive(true);
            this.postsService.generateJoiningPost(user);
            this.userRepository.save(user);
            this.emailSenderService.sendApprovedNotificationEmailToUser(user);
        } else {
            this.userRepository.deleteById(user.getId());
            this.emailSenderService.sendRejectedNotificationEmailToUser(user, rejectDTO);
        }

        return new ResponseEntity<>(
                new ResponseDTO("User " + (approved ? "approved" : "rejected") + " succesfully."),
                HttpStatus.OK
        );

    }

    public ResponseEntity<ResponseDTO> performAccountStatusSwitch(int uid, boolean active)
            throws UserNotFoundException, UserNotApprovedException, DuplicateResourceException {
        User user = this.userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("No user found with the given uid. " +
                        "Can't perform the " + (active ? "activation." : "inactivation.")));

        if (!user.isApproved()) {
            throw new UserNotApprovedException("You can't " +
                    (active ? "activate" : "inactive") +
                    " an unapproved user.");
        }

        if (user.isActive() && active) {
            throw new DuplicateResourceException("User already active.");
        }

        if(!user.isActive() && !active) {
            throw new DuplicateResourceException("User already inactive.");
        }

        user.setActive(active);

        this.userRepository.save(user);

        if (!active) {
            this.tokenService.revokeAllTokensForUser(uid);
        }

        return new ResponseEntity<>(
                new ResponseDTO("User " + (active ? "activated" : "inactivated") + " succesfully."),
                HttpStatus.OK
        );
    }

    public void save(User newUser) {
        this.userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public ResponseEntity<ResponseDTO> changePassword(ForgotPasswordDTO forgotPasswordDTO, String email)
            throws ThePasswordsDoNotMatchException, UserNotFoundException {

        Optional<User> userOptional = this.userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(() -> new UserNotFoundException("No user found with the given email. Can't perform the password change."));

        if (!forgotPasswordDTO.getPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
            throw new ThePasswordsDoNotMatchException("The passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(forgotPasswordDTO.getPassword()));

        userRepository.save(user);

        return new ResponseEntity<>(
                new ResponseDTO("Password changed."),
                HttpStatus.OK
        );
    }


    public List<UserProfileDTO> getRegistersRequestsWaitingForApprove() {
        return UserProfileDTO.fromUserListToUserProfileList(this.userRepository.findByApproved(false));
    }

    public List<UserProfileDTO> getUsersByStatus(Principal principal, boolean active) {
        return UserProfileDTO.fromUserListToUserProfileList(this.userRepository.findByActive(active))
                .stream().filter((userProfileDTO -> !Objects.equals(userProfileDTO.getRole(), Roles.ADMIN.name())
                        && !Objects.equals(userProfileDTO.getEmail(), principal.getName())
                        && userProfileDTO.isApproved())).toList();
    }

    public UserProfileDTO getUserProfileById(int id) {
        User user = this.userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User not found!"));
        return UserProfileDTO.fromUserEntity(user);
    }

    public ResponseSearchDTO getListSearch(String searchData, int offset, int size) {
        List<User> userList;
        int sizeDataSearch;
        int sizeCalc;

        if (searchData.isEmpty()) {
            sizeDataSearch = this.userRepository.countAll();
            userList = this.userRepository.findAllUserSearch(size, offset);
            sizeCalc = sizeDataSearch / size;
            if (sizeDataSearch % size != 0) {
                sizeCalc += 1;
            }
            return new ResponseSearchDTO(sizeDataSearch, sizeCalc, UserProfileDTO.fromUserListToUserProfileList(userList));
        }

        sizeDataSearch = this.userRepository.countAllSearch(searchData.toLowerCase());
        userList = this.userRepository.searchUsersData(searchData.toLowerCase(), size, offset);
        sizeCalc = sizeDataSearch / size;
        if (sizeDataSearch % size != 0) {
            sizeCalc += 1;
        }

        return new ResponseSearchDTO(sizeDataSearch, sizeCalc, UserProfileDTO.fromUserListToUserProfileList(userList));
    }

}
