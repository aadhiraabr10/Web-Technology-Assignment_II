<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    login.jsp
    ---------
    This is the login page. It is placed inside WEB-INF/views/ so that
    the browser CANNOT reach it directly by URL – it can only be shown
    by LoginServlet using a RequestDispatcher.forward().

    JSP Tags used here:
      <%@ page ... %>       – Page directive (sets MIME type and language)
      <%= ... %>            – Expression tag: prints a value
      ${ ... }             – EL (Expression Language): reads request attributes
      <% ... %>            – Scriptlet: Java code inside the page
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login – Student Profile Portal</title>
    <meta name="description"
          content="Sign in to access the Student Profile Portal and view student records." />
    <!-- Link to our stylesheet (located in webapp/css/style.css) -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body class="login-body">

    <div class="login-wrapper">

        <!-- ── Logo / Header ─────────────────────────────────────────── -->
        <div class="login-header">
            <div class="portal-icon" aria-hidden="true">🎓</div>
            <h1 class="login-title">Student Profile Portal</h1>
            <p class="login-subtitle">College Web Technologies Assignment</p>
        </div>

        <!-- ── Login Card ─────────────────────────────────────────────── -->
        <div class="login-card">

            <%-- Show a green "logged out" banner when ?logout=true is in the URL --%>
            <% if ("true".equals(request.getParameter("logout"))) { %>
                <div class="alert alert-success" role="alert">
                    ✔ You have been logged out successfully.
                </div>
            <% } %>

            <%-- Show a red error banner when LoginServlet sets errorMsg --%>
            <% if (request.getAttribute("errorMsg") != null) { %>
                <div class="alert alert-error" role="alert">
                    ⚠ <%= request.getAttribute("errorMsg") %>
                </div>
            <% } %>

            <!-- The form's action points to LoginServlet via the context path -->
            <form id="loginForm"
                  action="${pageContext.request.contextPath}/login"
                  method="post"
                  novalidate>

                <div class="form-group">
                    <label for="username">Username</label>
                    <input  type="text"
                            id="username"
                            name="username"
                            class="form-control"
                            placeholder="Enter your username"
                            autocomplete="username"
                            required />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input  type="password"
                            id="password"
                            name="password"
                            class="form-control"
                            placeholder="Enter your password"
                            autocomplete="current-password"
                            required />
                </div>

                <button type="submit" id="loginBtn" class="btn btn-primary btn-block">
                    Sign In
                </button>

            </form>

        </div><!-- /.login-card -->

        <footer class="login-footer">
            <p>&copy; 2024 Student Profile Portal &bull; Web Technologies Lab</p>
        </footer>

    </div><!-- /.login-wrapper -->

</body>
</html>
