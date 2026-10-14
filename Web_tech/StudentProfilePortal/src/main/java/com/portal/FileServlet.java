package com.portal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
@WebServlet("/files")
public class FileServlet extends HttpServlet {
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "pdf");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "You must be logged in to access this resource.");
            return;
        }
        String fileName = request.getParameter("name");

        if (fileName == null || fileName.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing 'name' parameter.");
            return;
        }
        if (!fileName.matches("[a-zA-Z0-9._\\-]+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid file name.");
            return;
        }
        String extension = getExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "File type not permitted.");
            return;
        }
        File uploadsDir = StudentUploadStorage.UPLOADS_DIR.toFile();
        File file = new File(uploadsDir, fileName).getCanonicalFile();
        if (!file.getAbsolutePath().startsWith(uploadsDir.getCanonicalPath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied.");
            return;
        }

        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "File not found.");
            return;
        }

        // ---- STREAM THE FILE TO THE BROWSER ---------------------------------
        String contentType = resolveContentType(extension);
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());

        // For PDFs opened in the browser (inline), for downloads use "attachment"
        String disposition = extension.equalsIgnoreCase("pdf")
                ? "inline; filename=\"" + fileName + "\""
                : "inline";
        response.setHeader("Content-Disposition", disposition);

        // Read the file and write it to the HTTP response output stream
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192]; // 8 KB buffer for efficiency
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // ---------- Helper: extract file extension --------------------------------

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot == -1 || dot == fileName.length() - 1) return "";
        return fileName.substring(dot + 1);
    }

    // ---------- Helper: map extension to MIME type ----------------------------

    private String resolveContentType(String ext) {
        return switch (ext.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png"         -> "image/png";
            case "gif"         -> "image/gif";
            case "pdf"         -> "application/pdf";
            default            -> "application/octet-stream";
        };
    }
}
