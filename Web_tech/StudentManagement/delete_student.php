<?php
require 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit("POST request required.");
}

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);

if (!$id) {
    exit("Invalid student ID.");
}

$stmt = $conn->prepare(
    "SELECT stored_name FROM student_files WHERE student_id=?"
);
$stmt->bind_param("i", $id);
$stmt->execute();
$result = $stmt->get_result();

$files = [];

while ($row = $result->fetch_assoc()) {
    $files[] = $row['stored_name'];
}
$stmt->close();

$stmt = $conn->prepare("DELETE FROM students WHERE id=?");
$stmt->bind_param("i", $id);
$stmt->execute();
$deleted = $stmt->affected_rows;
$stmt->close();

if ($deleted > 0) {
    $uploadDir = 'C:/xampp/student_uploads/';

    foreach ($files as $filename) {
        if (preg_match('/\A[a-f0-9]{32}\.(jpg|png|pdf)\z/', $filename)) {
            $path = $uploadDir . $filename;

            if (is_file($path)) {
                unlink($path);
            }
        }
    }
}

header("Location: index.php");
exit;
?>
