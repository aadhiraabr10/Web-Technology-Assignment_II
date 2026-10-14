package com.portal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
        @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); 
        if (session != null && session.getAttribute("loggedInUser") != null) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            request.setAttribute("errorMsg", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                   .forward(request, response);
            return;
        }
        String sql = "SELECT password_hash FROM users WHERE username = ?";        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {            ps.setString(1, username.trim()); 
            ResultSet rs = ps.executeQuery();            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (storedHash != null && !storedHash.isBlank()) {
                    try {
                        if (BCrypt.checkpw(password, storedHash)) {
                            HttpSession session = request.getSession(true); 
                            session.setAttribute("loggedInUser", username.trim());
                            session.setMaxInactiveInterval(30 * 60); 
                            response.sendRedirect(request.getContextPath() + "/students");
                            return;
                        }
                    } catch (IllegalArgumentException ex) {
                        getServletContext().log("Invalid BCrypt hash for user: " + username, ex);
                    }
                }
            }
            request.setAttribute("errorMsg", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                   .forward(request, response);        } catch (SQLException e) {
            getServletContext().log("LoginServlet DB error", e);
            request.setAttribute("errorMsg",
                "A database error occurred. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
