<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Add Student – Student Profile Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
<header class="topbar">
    <div class="topbar-inner">
        <a class="topbar-brand" href="${pageContext.request.contextPath}/students">
            <span class="brand-icon" aria-hidden="true">🎓</span>
            <span class="brand-name">Student Profile Portal</span>
        </a>
        <nav class="topbar-nav">
            <a href="${pageContext.request.contextPath}/students" class="btn btn-outline btn-sm">
                ← Back to Students
            </a>
            <span class="welcome-text">Welcome, <strong><c:out value="${loggedInUser}" /></strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm">Logout</a>
        </nav>
    </div>
</header>

<main class="main-content form-page">
    <div class="page-header">
        <h1 class="page-title">Add Student Details</h1>
        <p class="page-subtitle">Enter the student information below. Fields marked * are required.</p>
    </div>

    <section class="student-form-card" aria-labelledby="studentFormTitle">
        <h2 id="studentFormTitle" class="form-card-title">Student Information</h2>
        <c:if test="${not empty errorMsg}">
            <div class="alert alert-error" role="alert"><c:out value="${errorMsg}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/students/add"
              method="post" enctype="multipart/form-data">
            <div class="student-form-grid">
                <div class="form-group">
                    <label for="rollNo">Roll Number *</label>
                    <input class="form-control" id="rollNo" name="rollNo" type="text"
                           maxlength="20" placeholder="e.g. CS2025001"
                           value="<c:out value='${rollNo}' />" required />
                </div>
                <div class="form-group">
                    <label for="name">Full Name *</label>
                    <input class="form-control" id="name" name="name" type="text"
                           maxlength="100" placeholder="Enter student's full name"
                           value="<c:out value='${studentName}' />" required />
                </div>
                <div class="form-group">
                    <label for="department">Department *</label>
                    <input class="form-control" id="department" name="department" type="text"
                           maxlength="100" placeholder="e.g. Computer Science"
                           value="<c:out value='${department}' />" required />
                </div>
                <div class="form-group">
                    <label for="email">Email Address *</label>
                    <input class="form-control" id="email" name="email" type="email"
                           maxlength="150" placeholder="student@example.com"
                           value="<c:out value='${email}' />" required />
                </div>
                <div class="form-group">
                    <label for="photo">Student Photo <span class="optional-label">Optional</span></label>
                    <input class="form-control file-control" id="photo" name="photo" type="file"
                           accept=".jpg,.jpeg,.png,.gif,image/jpeg,image/png,image/gif" />
                    <small class="field-hint">JPG, PNG, or GIF · maximum 5 MB</small>
                </div>
                <div class="form-group">
                    <label for="document">Document <span class="optional-label">Optional</span></label>
                    <input class="form-control file-control" id="document" name="document" type="file"
                           accept=".pdf,application/pdf" />
                    <small class="field-hint">PDF only · maximum 5 MB</small>
                </div>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">Save Student</button>
            </div>
        </form>
    </section>
</main>

<footer class="site-footer">
    <p>&copy; 2024 Student Profile Portal &bull; Web Technologies Lab</p>
</footer>
</body>
</html>
