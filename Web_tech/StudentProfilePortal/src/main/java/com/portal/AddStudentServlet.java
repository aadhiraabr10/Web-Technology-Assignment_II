package com.portal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;@WebServlet("/students/add")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 11 * 1024 * 1024
)
public class AddStudentServlet extends HttpServlet {
    private static final Set<String> PHOTO_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("pdf");    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireLogin(request, response)) {
            return;
        }
        request.setAttribute("loggedInUser", request.getSession(false).getAttribute("loggedInUser"));
        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
    }    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireLogin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");        String rollNo = trim(request.getParameter("rollNo"));
        String name = trim(request.getParameter("name"));
        String department = trim(request.getParameter("department"));
        String email = trim(request.getParameter("email"));
        if (rollNo.isEmpty() || name.isEmpty() || department.isEmpty() || email.isEmpty()) {
            showForm(request, response, "Please complete all required student details.",
                    rollNo, name, department, email);
            return;
        }
        if (rollNo.length() > 20 || name.length() > 100 || department.length() > 100
                || email.length() > 150 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showForm(request, response, "Check the field lengths and enter a valid email address.",
                    rollNo, name, department, email);
            return;
        }        String photoName = null;
        String documentName = null;
        boolean saved = false;
        try {
            Part photo = request.getPart("photo");
            Part document = request.getPart("document");
            photoName = StudentUploadStorage.save(photo, PHOTO_EXTENSIONS);
            documentName = StudentUploadStorage.save(document, DOCUMENT_EXTENSIONS);            String sql = "INSERT INTO students "
                    + "(roll_no, name, department, email, photo_path, document_path) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, rollNo);
                statement.setString(2, name);
                statement.setString(3, department);
                statement.setString(4, email);
                statement.setString(5, photoName);
                statement.setString(6, documentName);
                statement.executeUpdate();
            }
            saved = true;
            response.sendRedirect(request.getContextPath() + "/students?added=true");
        } catch (IllegalArgumentException e) {
            showForm(request, response, e.getMessage(), rollNo, name, department, email);
        } catch (IllegalStateException e) {
            showForm(request, response, "Each uploaded file must be 5 MB or smaller.",
                    rollNo, name, department, email);
        } catch (SQLException e) {
            getServletContext().log("AddStudentServlet DB error", e);
            String message = e.getSQLState() != null && e.getSQLState().startsWith("23")
                    ? "That roll number is already registered."
                    : "Could not save the student details. Please try again.";
            showForm(request, response, message, rollNo, name, department, email);
        } catch (IOException e) {
            getServletContext().log("AddStudentServlet upload error", e);
            showForm(request, response, "Could not save the uploaded files. Please try again.",
                    rollNo, name, department, email);
        } finally {
            if (!saved) {
                deleteUpload(photoName);
                deleteUpload(documentName);
            }
        }
    }    private boolean requireLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }    private void showForm(HttpServletRequest request, HttpServletResponse response,
                          String error, String rollNo, String name,
                          String department, String email)
            throws ServletException, IOException {
        request.setAttribute("errorMsg", error);
        request.setAttribute("rollNo", rollNo);
        request.setAttribute("studentName", name);
        request.setAttribute("department", department);
        request.setAttribute("email", email);
        request.setAttribute("loggedInUser", request.getSession(false).getAttribute("loggedInUser"));
        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
    }    private void deleteUpload(String fileName) {
        try {
            StudentUploadStorage.delete(fileName);
        } catch (IOException e) {
            getServletContext().log("Could not remove unused student upload: " + fileName, e);
        }
    }    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
