package com.food.ordering.system.application.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PathValidator {

    private static final Pattern SUSPICIOUS_PATH_PATTERN = Pattern.compile(
        ".*[/\\\\]\\.\\.([/\\\\].*)?$|.*[/\\\\]\\.([/\\\\].*)?$|.*\\.(bat|cmd|exe|sh|ps1|vbs|jar)$",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]{1,255}$"
    );

    public boolean isValidPath(String inputPath) {
        if (inputPath == null || inputPath.trim().isEmpty()) {
            log.warn("Path validation failed: null or empty path");
            return false;
        }

        try {
            // Check for suspicious patterns
            if (SUSPICIOUS_PATH_PATTERN.matcher(inputPath).matches()) {
                log.warn("Path validation failed: suspicious pattern detected in path: {}", inputPath);
                return false;
            }

            // Check for null bytes
            if (inputPath.contains("\0")) {
                log.warn("Path validation failed: null byte detected in path: {}", inputPath);
                return false;
            }

            // Normalize the path to detect traversal attempts
            Path normalizedPath = Paths.get(inputPath).normalize();
            String normalizedString = normalizedPath.toString();

            // Check if normalization revealed path traversal
            if (normalizedString.contains("..")) {
                log.warn("Path validation failed: path traversal detected after normalization: {}", normalizedString);
                return false;
            }

            // Additional checks for absolute paths that shouldn't be allowed
            if (normalizedPath.isAbsolute()) {
                log.warn("Path validation failed: absolute path not allowed: {}", normalizedString);
                return false;
            }

            return true;

        } catch (InvalidPathException e) {
            log.warn("Path validation failed: invalid path format: {}", inputPath, e);
            return false;
        } catch (Exception e) {
            log.warn("Path validation failed: unexpected error: {}", inputPath, e);
            return false;
        }
    }

    public boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        return VALID_FILENAME_PATTERN.matcher(filename).matches() &&
               !filename.equals(".") &&
               !filename.equals("..") &&
               !filename.contains("/") &&
               !filename.contains("\\");
    }

    public String sanitizePath(String inputPath) {
        if (!isValidPath(inputPath)) {
            throw new IllegalArgumentException("Invalid or unsafe path: " + inputPath);
        }

        try {
            return Paths.get(inputPath).normalize().toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to sanitize path: " + inputPath, e);
        }
    }

    public String sanitizeFilename(String filename) {
        if (!isValidFilename(filename)) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }

        // Remove any potential harmful characters and keep only safe ones
        return filename.replaceAll("[^a-zA-Z0-9._-]", "");
    }
}