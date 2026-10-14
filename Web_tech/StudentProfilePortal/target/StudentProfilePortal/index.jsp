<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    index.jsp  –  placed at the webapp root
    Immediately redirects anyone visiting the app root to /login.
    This ensures the very first thing a visitor sees is the login page.
--%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
