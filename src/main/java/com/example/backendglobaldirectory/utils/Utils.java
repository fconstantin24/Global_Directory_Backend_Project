package com.example.backendglobaldirectory.utils;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Utils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String ANNIVERSARY_EMAIL_PATTERN_PATH = "src/main/resources/anniversary_mail_pattern.txt";
    private static final String PROMOTION_EMAIL_PATTERN_PATH = "src/main/resources/promotion_mail_pattern.txt";
    public static final String REJECT_EMAIL_PATTERN_PATH = "src/main/resources/reject_mail_pattern.txt";
    public static final String APPROVE_EMAIL_PATTERN_PATH = "src/main/resources/approve_mail_pattern.txt";
    private static final String RESET_PASSWORD_EMAIL_PATTERN_PATH = "src/main/resources/reset_password_mail_pattern.txt";
    ;

    public static Optional<LocalDateTime> convertDateStringToLocalDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        LocalDate date = LocalDate.parse(dateString, formatter);
        LocalTime time = LocalTime.of(0, 0);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        LocalDateTime currentTime = LocalDateTime.now();

        if (dateTime.isAfter(currentTime)) {
            return Optional.empty();
        }

        return Optional.of(dateTime);
    }

    public static String readMailPattern(String filePath) {
        StringBuilder mailFormatBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mailFormatBuilder.append(line);
                mailFormatBuilder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return mailFormatBuilder.toString();
    }

    public static String readAnniversaryMailPattern() {
        return readMailPattern(ANNIVERSARY_EMAIL_PATTERN_PATH);
    }

    public static String readPromotionMailPattern() {
        return readMailPattern(PROMOTION_EMAIL_PATTERN_PATH);
    }

    public static String readRejectMailPattern() {
        return readMailPattern(REJECT_EMAIL_PATTERN_PATH);
    }

    public static String readApproveMailPattern() {
        return readMailPattern(APPROVE_EMAIL_PATTERN_PATH);
    }

    public static String readResetMailPattern() { return readMailPattern(RESET_PASSWORD_EMAIL_PATTERN_PATH); }

    public static long getPeriodOfTimeInMinutesFrom(LocalDateTime initial) {
        return Duration.between(initial, LocalDateTime.now()).toMinutes();
    }

    public static String getPeriodOfTimeAsString(long minutes) {
        StringBuilder periodStringBuilder = new StringBuilder();

        Duration duration = Duration.ofMinutes(minutes);

        if (minutes == 0) {
            periodStringBuilder.append("Just now");
        } else if (minutes < 60) {
            periodStringBuilder.append(minutes);
            periodStringBuilder.append(" m ago");
        } else if (minutes <= 1440) {
            periodStringBuilder.append(duration.toHours());
            periodStringBuilder.append(" h ago");
        } else {
            periodStringBuilder.append(duration.toDays());
            periodStringBuilder.append(" d ago");
        }

        return periodStringBuilder.toString();
    }


}
