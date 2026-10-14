<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.portal.Student" %>
<%@ page import="java.util.List" %>
<%--
    students.jsp
    ------------
    Displays all student records in a responsive card grid.

    Data flow:
      StudentListServlet → sets request attribute "students" (List<Student>)
      → forwards here → JSP loops over the list and renders each card

    Tags & concepts used:
      <%@ page import="..." %>   – makes Java classes available in this JSP
      <% List<Student> ... %>    – scriptlet: cast the attribute to its type
      <% for(...) { %>           – loop over every Student
      <%= student.getName() %>   – print a Student field value
      ${pageContext.request.contextPath}  – gives the app's base URL path
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Student Profiles – Student Profile Portal</title>
    <meta name="description"
          content="View all registered student profiles including department, email, photo and documents." />
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body class="students-body">

<%
    // ── Fetch the data that StudentListServlet put in the request ──────────
    @SuppressWarnings("unchecked")
    List<Student> students = (List<Student>) request.getAttribute("students");
    String loggedInUser    = (String) request.getAttribute("loggedInUser");
    String dbError         = (String) request.getAttribute("dbError");
    String contextPath     = request.getContextPath();
%>

<!-- ── Top Navigation Bar ──────────────────────────────────────────────── -->
<header class="topbar">
    <div class="topbar-inner">
        <div class="topbar-brand">
            <span class="brand-icon" aria-hidden="true">🎓</span>
            <span class="brand-name">Student Profile Portal</span>
        </div>
        <nav class="topbar-nav">
            <span class="welcome-text">
                Welcome, <strong><%= loggedInUser != null ? loggedInUser : "User" %></strong>
            </span>
            <a href="<%= contextPath %>/logout"
               id="logoutLink"
               class="btn btn-outline btn-sm"
               onclick="return confirm('Are you sure you want to logout?');">
                Logout
            </a>
        </nav>
    </div>
</header>

<!-- ── Main Content ─────────────────────────────────────────────────────── -->
<main class="main-content">

    <div class="page-header">
        <div>
            <h1 class="page-title">Student Profiles</h1>
            <p class="page-subtitle">
                <% if (students != null) { %>
                    Showing <strong><%= students.size() %></strong>
                    registered student<%= students.size() != 1 ? "s" : "" %>.
                <% } %>
            </p>
        </div>
        <a href="<%= contextPath %>/students/add" class="btn btn-primary">＋ Add Student</a>
    </div>

    <% if ("true".equals(request.getParameter("added"))) { %>
        <div class="alert alert-success" role="status">Student details saved successfully.</div>
    <% } %>

    <% if ("true".equals(request.getParameter("deleted"))) { %>
        <div class="alert alert-success" role="status">Student deleted successfully.</div>
    <% } %>

    <% String deleteError = request.getParameter("deleteError"); %>
    <% if (deleteError != null) { %>
        <div class="alert alert-error" role="alert">
            <% if ("missing".equals(deleteError)) { %>
                That student was not found. The list may have changed.
            <% } else if ("invalid".equals(deleteError)) { %>
                The student selection was invalid.
            <% } else { %>
                Could not delete the student. Please try again.
            <% } %>
        </div>
    <% } %>

    <%-- Database error message --%>
    <% if (dbError != null) { %>
        <div class="alert alert-error">⚠ <%= dbError %></div>
    <% } %>

    <%-- Empty state --%>
    <% if (students == null || students.isEmpty()) { %>
        <div class="empty-state">
            <div class="empty-icon" aria-hidden="true">📭</div>
            <h2>No students found</h2>
            <p>Add student records to the database to see them here.</p>
            <a href="<%= contextPath %>/students/add" class="btn btn-primary">＋ Add Student</a>
        </div>

    <% } else { %>

    <!-- ── Student Card Grid ──────────────────────────────────────────── -->
    <div class="student-grid" id="studentGrid">

        <% for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
        %>
        <article class="student-card" id="card-<%= student.getId() %>">

            <!-- Photo Section -->
            <div class="card-photo-wrap">
                <% if (student.getPhotoPath() != null && !student.getPhotoPath().isBlank()) { %>
                    <%-- Photo exists: <img> src points to FileServlet --%>
                    <img src="<%= contextPath %>/files?name=<%= student.getPhotoPath() %>"
                         alt="Photo of <%= student.getName() %>"
                         class="card-photo"
                         loading="lazy"
                         onerror="this.style.display='none';
                                  this.nextElementSibling.style.display='flex';" />
                    <div class="photo-placeholder" style="display:none;" aria-hidden="true">
                        <span class="avatar-initials">
                            <%= student.getName().length() > 0
                                ? String.valueOf(student.getName().charAt(0)).toUpperCase()
                                : "?" %>
                        </span>
                    </div>
                <% } else { %>
                    <%-- No photo: show initials avatar --%>
                    <div class="photo-placeholder" aria-label="No photo available">
                        <span class="avatar-initials">
                            <%= student.getName().length() > 0
                                ? String.valueOf(student.getName().charAt(0)).toUpperCase()
                                : "?" %>
                        </span>
                    </div>
                <% } %>
            </div><!-- /.card-photo-wrap -->

            <!-- Student Info -->
            <div class="card-body">
                <h2 class="student-name"><%= student.getName() %></h2>

                <div class="student-meta">
                    <div class="meta-row">
                        <span class="meta-label">Roll No</span>
                        <span class="meta-value badge"><%= student.getRollNo() %></span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Department</span>
                        <span class="meta-value"><%= student.getDepartment() %></span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Email</span>
                        <a href="mailto:<%= student.getEmail() %>"
                           class="meta-value email-link"><%= student.getEmail() %></a>
                    </div>
                </div>

                <!-- Document / PDF link -->
                <div class="card-actions">
                    <% if (student.getDocumentPath() != null && !student.getDocumentPath().isBlank()) { %>
                        <a href="<%= contextPath %>/files?name=<%= student.getDocumentPath() %>"
                           target="_blank"
                           rel="noopener noreferrer"
                           class="btn btn-doc"
                           title="Open <%= student.getName() %>'s document">
                            📄 View Document
                        </a>
                    <% } else { %>
                        <span class="no-doc-msg">📭 No document uploaded</span>
                    <% } %>
                    <form action="<%= contextPath %>/students/delete" method="post"
                          onsubmit="return confirm('Delete this student permanently?');">
                        <input type="hidden" name="id" value="<%= student.getId() %>" />
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>

            </div><!-- /.card-body -->
        </article>
        <% } /* end for loop */ %>

    </div><!-- /#studentGrid -->

    <% } /* end if students */ %>

</main><!-- /.main-content -->

<footer class="site-footer">
    <p>&copy; 2024 Student Profile Portal &bull; Web Technologies Lab</p>
</footer>

</body>
</html>
