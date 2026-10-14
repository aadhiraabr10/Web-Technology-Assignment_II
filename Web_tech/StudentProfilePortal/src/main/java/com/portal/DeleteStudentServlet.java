package com.portal;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;@WebServlet("/students/delete")
public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }        int studentId;
        try {
            studentId = Integer.parseInt(request.getParameter("id"));
            if (studentId < 1) {
                throw new NumberFormatException("Student ID must be positive");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/students?deleteError=invalid");
            return;
        }        String photoPath;
        String documentPath;
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement select = connection.prepareStatement(
                        "SELECT photo_path, document_path FROM students WHERE id = ?")) {
                    select.setInt(1, studentId);
                    try (ResultSet result = select.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            response.sendRedirect(request.getContextPath() + "/students?deleteError=missing");
                            return;
                        }
                        photoPath = result.getString("photo_path");
                        documentPath = result.getString("document_path");
                    }
                }                try (PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM students WHERE id = ?")) {
                    delete.setInt(1, studentId);
                    if (delete.executeUpdate() != 1) {
                        connection.rollback();
                        response.sendRedirect(request.getContextPath() + "/students?deleteError=missing");
                        return;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            getServletContext().log("DeleteStudentServlet DB error for student ID " + studentId, e);
            response.sendRedirect(request.getContextPath() + "/students?deleteError=failed");
            return;
        }        deleteUpload(photoPath);
        deleteUpload(documentPath);
        response.sendRedirect(request.getContextPath() + "/students?deleted=true");
    }    private void deleteUpload(String fileName) {
        try {
            StudentUploadStorage.delete(fileName);
        } catch (IOException e) {
            getServletContext().log("Could not remove deleted student's upload: " + fileName, e);
        }
    }
}
