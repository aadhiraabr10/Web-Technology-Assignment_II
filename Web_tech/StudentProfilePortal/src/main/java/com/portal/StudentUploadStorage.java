package com.portal;import jakarta.servlet.http.Part;import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;final class StudentUploadStorage {
    static final Path UPLOADS_DIR = Path.of("C:/uploads/student_portal");    private StudentUploadStorage() {
    }    static String save(Part part, Set<String> allowedExtensions) throws IOException {
        if (part == null) {
            return null;
        }
        String submittedName = part.getSubmittedFileName();
        if (part.getSize() == 0 || submittedName == null || submittedName.isBlank()) {
            return null;
        }        String originalName = Path.of(submittedName.replace('\\', '/')).getFileName().toString();
        int dot = originalName.lastIndexOf('.');
        if (dot < 0 || dot == originalName.length() - 1) {
            throw new IllegalArgumentException("Uploaded files must have an allowed file extension.");
        }        String extension = originalName.substring(dot + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Please choose a supported file type.");
        }        Files.createDirectories(UPLOADS_DIR);
        String storedName = UUID.randomUUID() + "." + extension;
        Path destination = UPLOADS_DIR.resolve(storedName).normalize();
        if (!destination.startsWith(UPLOADS_DIR.normalize())) {
            throw new IOException("Invalid upload destination.");
        }        try (var input = part.getInputStream()) {
            Files.copy(input, destination);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(destination);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }
        return storedName;
    }    static void delete(String fileName) throws IOException {
        if (fileName == null) {
            return;
        }
        Path file = Path.of(fileName);
        if (file.isAbsolute() || file.getNameCount() != 1
                || !file.getFileName().toString().equals(fileName)) {
            throw new IOException("Invalid stored upload name.");
        }
        Path uploadsDirectory = UPLOADS_DIR.toAbsolutePath().normalize();
        Path target = uploadsDirectory.resolve(file).normalize();
        if (!target.startsWith(uploadsDirectory)) {
            throw new IOException("Upload path is outside the upload directory.");
        }
        Files.deleteIfExists(target);
    }
}
