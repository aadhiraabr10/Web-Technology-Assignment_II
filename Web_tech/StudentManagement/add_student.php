<?php
require 'db.php';

$uploadDir = 'C:/xampp/student_uploads/';

function failRequest($message) {
    http_response_code(400);
    exit(htmlspecialchars($message));
}

$name = trim($_POST['name'] ?? '');
$roll = trim($_POST['roll_number'] ?? '');
$email = trim($_POST['email'] ?? '');
$course = trim($_POST['course'] ?? '');

if ($name === '' || $roll === '' ||
    $email === '' || $course === '') {
    failRequest("Please fill in all required fields.");
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    failRequest("Enter a valid email address.");
}

$stmt = $conn->prepare(
    "INSERT INTO students (name, roll_number, email, course)
     VALUES (?, ?, ?, ?)"
);

$stmt->bind_param("ssss", $name, $roll, $email, $course);

if (!$stmt->execute()) {
    if ($conn->errno === 1062) {
        failRequest("This roll number already exists.");
    }
    failRequest("Unable to save student.");
}

$studentId = $conn->insert_id;
$stmt->close();

if (isset($_FILES['document']) &&
    $_FILES['document']['error'] !== UPLOAD_ERR_NO_FILE) {

    $file = $_FILES['document'];

    if ($file['error'] !== UPLOAD_ERR_OK) {
        failRequest("File upload failed.");
    }

    if ($file['size'] <= 0 ||
        $file['size'] > 2 * 1024 * 1024) {
        failRequest("File must be smaller than 2 MB.");
    }

    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime = $finfo->file($file['tmp_name']);

    $allowed = [
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'application/pdf' => 'pdf'
    ];

    if (!isset($allowed[$mime])) {
        failRequest("Only JPG, PNG and PDF files are allowed.");
    }

    if (!is_dir($uploadDir) &&
        !mkdir($uploadDir, 0750, true)) {
        failRequest("Cannot create upload folder.");
    }

    $storedName = bin2hex(random_bytes(16))
                  . '.' . $allowed[$mime];

    $destination = $uploadDir . $storedName;

    if (!move_uploaded_file($file['tmp_name'], $destination)) {
        failRequest("Could not store uploaded file.");
    }

    $relativePath = 'student_uploads/' . $storedName;
    $originalName = basename($file['name']);

    $fileStmt = $conn->prepare(
        "INSERT INTO student_files
        (student_id, original_name, stored_name,
         file_path, file_type, file_size)
        VALUES (?, ?, ?, ?, ?, ?)"
    );

    $fileSize = (int)$file['size'];

    $fileStmt->bind_param(
        "issssi",
        $studentId,
        $originalName,
        $storedName,
        $relativePath,
        $mime,
        $fileSize
    );

    if (!$fileStmt->execute()) {
        unlink($destination);
        $fileStmt->close();
        failRequest("Could not save file information.");
    }

    $fileStmt->close();
}

header("Location: index.php");
exit;
?>
