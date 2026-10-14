<?php
require 'db.php';

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);

if (!$id) {
    http_response_code(400);
    exit("Invalid file ID.");
}

$stmt = $conn->prepare(
    "SELECT stored_name, file_type, original_name
     FROM student_files WHERE id=?"
);
$stmt->bind_param("i", $id);
$stmt->execute();
$file = $stmt->get_result()->fetch_assoc();
$stmt->close();
$conn->close();

if (!$file ||
    !preg_match('/\A[a-f0-9]{32}\.(jpg|png|pdf)\z/',
                 $file['stored_name'])) {
    http_response_code(404);
    exit("File not found.");
}

$allowed = [
    'jpg' => 'image/jpeg',
    'png' => 'image/png',
    'pdf' => 'application/pdf'
];

$extension = pathinfo($file['stored_name'], PATHINFO_EXTENSION);

if (!isset($allowed[$extension]) ||
    $allowed[$extension] !== $file['file_type']) {
    http_response_code(403);
    exit("Invalid file type.");
}

$path = 'C:/xampp/student_uploads/' . $file['stored_name'];

if (!is_file($path)) {
    http_response_code(404);
    exit("Stored file is missing.");
}

header('X-Content-Type-Options: nosniff');
header('Content-Type: ' . $allowed[$extension]);

if (isset($_GET['download'])) {
    header('Content-Disposition: attachment; filename="'
        . $file['stored_name'] . '"');
} else {
    header('Content-Disposition: inline; filename="'
        . $file['stored_name'] . '"');
}

header('Content-Length: ' . filesize($path));
readfile($path);
exit;
?>
