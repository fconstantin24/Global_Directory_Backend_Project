package com.example.backendglobaldirectory.service;

import com.example.backendglobaldirectory.dto.RejectDTO;
import com.example.backendglobaldirectory.entities.Token;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.exception.UserNotFoundException;
import com.example.backendglobaldirectory.repository.TokenRepository;
import com.example.backendglobaldirectory.repository.UserRepository;
import com.example.backendglobaldirectory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor asyncTaskExecutor;

    public Map<String, String> createEmail(String email)
            throws UserNotFoundException, FileNotFoundException {
        Optional<User> userOptional = this.userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("No user found with the given email. Can't perform the password change.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", List.of(userOptional.get().getRole().name()));

        String jwtToken = this.jwtService.generateToken(claims, userOptional.get());

        Token token = new Token(jwtToken,
                false,
                false,
                userOptional.get());

        this.tokenRepository.save(token);

        String link = "http://localhost:4200/reset-password?token=" + jwtToken;

        String resetPasswordMailFormat = Utils.readResetMailPattern();

        String emailBody = String.format(
                resetPasswordMailFormat,
                userOptional.get().getFirstName() + " " + userOptional.get().getLastName(),
                link);

        return sendEmail(userOptional.get().getEmail(), "Reset password email", emailBody);
    }

    public void sendAnniversaryEmailToUser(User user, int noOfYears) {
        String anniversaryMailFormat = Utils.readAnniversaryMailPattern();

        if (anniversaryMailFormat == null) {
            return;
        }

        String emailBody = String.format(
                anniversaryMailFormat,
                user.getFirstName() + " " + user.getLastName(),
                noOfYears);

        asyncTaskExecutor.execute(() -> sendEmail(
                user.getEmail(),
                "Anniversary email",
                emailBody
        ));
    }

    public void sendPromotionEmailToUser(User user, String newJobTitle) {
        String promotionMailFormat = Utils.readPromotionMailPattern();

        if (promotionMailFormat == null) {
            return;
        }

        String emailBody = String.format(
                promotionMailFormat,
                user.getFirstName() + " " + user.getLastName(),
                newJobTitle);

        asyncTaskExecutor.execute(() -> sendEmail(
                user.getEmail(),
                "Promotion email",
                emailBody
        ));
    }

    public void sendRejectedNotificationEmailToUser(User user, RejectDTO rejectDTO) {
        String rejectMailFormat = Utils.readRejectMailPattern();

        if (rejectMailFormat == null) {
            return;
        }

        String emailBody = String.format(
                rejectMailFormat,
                user.getFirstName() + " " + user.getLastName(),
                (rejectDTO.getReason() == null ? "-" : rejectDTO.getReason()),
                (rejectDTO.getDescription() == null ? "-" : rejectDTO.getDescription())
        );

        asyncTaskExecutor.execute(() -> sendEmail(
                user.getEmail(),
                "Register request rejected",
                emailBody
        ));
    }

    public void sendApprovedNotificationEmailToUser(User user) {
        String approvedMailFormat = Utils.readApproveMailPattern();

        if (approvedMailFormat == null) {
            return;
        }

        String emailBody = String.format(
                approvedMailFormat,
                user.getFirstName() + " " + user.getLastName()
        );

        asyncTaskExecutor.execute(() -> sendEmail(
                user.getEmail(),
                "Register request approved",
                emailBody
        ));

    }

    @Async
    public Map<String, String> sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mail sent successfully.");

        return response;
    }

}
