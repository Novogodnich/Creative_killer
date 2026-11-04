package ru.rootpoint.creative_ciller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LogService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static void error(String message) {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(formatter);

        System.out.println("[ERROR] " + formatted + ": " + message);
    }

    public static void info(String message) {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(formatter);

        System.out.println("[INFO] " + formatted + ": " + message);
    }

    public static void warning(String message) {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(formatter);

        System.out.println("[WARN] " + formatted + ": " + message);
    }

}
