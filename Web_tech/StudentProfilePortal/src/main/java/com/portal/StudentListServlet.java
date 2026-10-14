package com.portal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/students")
public class StudentListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, roll_no, name, department, email, "
                   + "photo_path, document_path FROM students ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("id"),
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("email"),
                    rs.getString("photo_path"),      
                    rs.getString("document_path")    
                );
                students.add(s);
            }
        } catch (SQLException e) {
            getServletContext().log("StudentListServlet DB error", e);
            request.setAttribute("dbError","Could not load student data. Please contact your administrator.");
        }
        request.setAttribute("students", students);
        request.setAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        request.getRequestDispatcher("/WEB-INF/views/students.jsp").forward(request, response);
    }
}
