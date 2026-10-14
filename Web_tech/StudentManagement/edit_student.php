<?php
require 'db.php';

$uploadDir = 'C:/xampp/student_uploads/';

function failEdit($message) {
    http_response_code(400);
    exit(htmlspecialchars($message));
}

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name = trim($_POST['name'] ?? '');
    $roll = trim($_POST['roll_number'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $course = trim($_POST['course'] ?? '');

    if (!$id || $name === '' || $roll === '' ||
        $course === '' ||
        !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        failEdit("Please enter valid student details.");
    }

    $stmt = $conn->prepare(
        "UPDATE students
         SET name=?, roll_number=?, email=?, course=?
         WHERE id=?"
    );
    $stmt->bind_param("ssssi", $name, $roll, $email, $course, $id);

    if (!$stmt->execute()) {
        if ($conn->errno === 1062) {
            failEdit("This roll number already exists.");
        }
        failEdit("Could not update student.");
    }
    $stmt->close();

    if (isset($_FILES['document']) &&
        $_FILES['document']['error'] !== UPLOAD_ERR_NO_FILE) {

        $file = $_FILES['document'];

        if ($file['error'] !== UPLOAD_ERR_OK ||
            $file['size'] <= 0 ||
            $file['size'] > 2 * 1024 * 1024) {
            failEdit("Upload failed or file exceeds 2 MB.");
        }

        $finfo = new finfo(FILEINFO_MIME_TYPE);
        $mime = $finfo->file($file['tmp_name']);

        $allowed = [
            'image/jpeg' => 'jpg',
            'image/png' => 'png',
            'application/pdf' => 'pdf'
        ];

        if (!isset($allowed[$mime])) {
            failEdit("Only JPG, PNG and PDF files are allowed.");
        }

        if (!is_dir($uploadDir) &&
            !mkdir($uploadDir, 0750, true)) {
            failEdit("Cannot create upload folder.");
        }

        $storedName = bin2hex(random_bytes(16))
                      . '.' . $allowed[$mime];
        $destination = $uploadDir . $storedName;

        if (!move_uploaded_file($file['tmp_name'], $destination)) {
            failEdit("Could not store uploaded file.");
        }

        $originalName = basename($file['name']);
        $fileSize = (int)$file['size'];
        $relativePath = 'student_uploads/' . $storedName;

        $fileStmt = $conn->prepare(
            "INSERT INTO student_files
             (student_id, original_name, stored_name,
              file_path, file_type, file_size)
             VALUES (?, ?, ?, ?, ?, ?)"
        );

        $fileStmt->bind_param(
            "issssi", $id, $originalName, $storedName,
            $relativePath, $mime, $fileSize
        );

        if (!$fileStmt->execute()) {
            unlink($destination);
            failEdit("Could not save file information.");
        }

        $fileStmt->close();
    }

    header("Location: index.php");
    exit;
}

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);

if (!$id) {
    failEdit("Invalid student ID.");
}

$stmt = $conn->prepare("SELECT * FROM students WHERE id=?");
$stmt->bind_param("i", $id);
$stmt->execute();
$student = $stmt->get_result()->fetch_assoc();
$stmt->close();

if (!$student) {
    http_response_code(404);
    exit("Student not found.");
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Student</title>
</head>
<body>
    <h2>Edit Student Details</h2>

    <form method="POST" enctype="multipart/form-data">
        <input type="hidden" name="id"
               value="<?= (int)$student['id'] ?>">

        <label>Name</label>
        <input name="name" maxlength="100" required
               value="<?= htmlspecialchars($student['name']) ?>">

        <label>Roll Number</label>
        <input name="roll_number" maxlength="30" required
               value="<?= htmlspecialchars($student['roll_number']) ?>">

        <label>Email</label>
        <input type="email" name="email" maxlength="120" required
               value="<?= htmlspecialchars($student['email']) ?>">

        <label>Course</label>
        <input name="course" maxlength="100" required
               value="<?= htmlspecialchars($student['course']) ?>">

        <label>Add another image/document (optional)</label>
        <input type="file" name="document"
               accept=".jpg,.jpeg,.png,.pdf">

        <button type="submit">Save Changes</button>
    </form>

    <p><a href="index.php">Back to Students</a></p>
</body>
</html>
