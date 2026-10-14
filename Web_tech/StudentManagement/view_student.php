<?php
require 'db.php';

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);

if (!$id) {
    http_response_code(400);
    exit("Invalid student ID.");
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

$stmt = $conn->prepare(
    "SELECT id, original_name, file_type, file_size, upload_date
     FROM student_files WHERE student_id=? ORDER BY id DESC"
);
$stmt->bind_param("i", $id);
$stmt->execute();
$files = $stmt->get_result();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Details</title>
</head>
<body>
    <h1>Student Profile</h1>

    <p><b>Name:</b>
        <?= htmlspecialchars($student['name']) ?></p>
    <p><b>Roll Number:</b>
        <?= htmlspecialchars($student['roll_number']) ?></p>
    <p><b>Email:</b>
        <?= htmlspecialchars($student['email']) ?></p>
    <p><b>Course:</b>
        <?= htmlspecialchars($student['course']) ?></p>

    <h2>Uploaded Images and Documents</h2>

    <?php if ($files->num_rows > 0) { ?>
        <?php while ($file = $files->fetch_assoc()) { ?>
            <div style="border:1px solid #ddd; padding:12px; margin:12px 0">
                <p>
                    <b>Filename:</b>
                    <?= htmlspecialchars($file['original_name']) ?>
                </p>

                <p>
                    <b>Type:</b>
                    <?= htmlspecialchars($file['file_type']) ?>
                </p>

                <p>
                    <b>Size:</b>
                    <?= number_format($file['file_size'] / 1024, 2) ?> KB
                </p>

                <p>
                    <b>Uploaded:</b>
                    <?= htmlspecialchars($file['upload_date']) ?>
                </p>

                <?php if (in_array($file['file_type'],
                    ['image/jpeg', 'image/png'], true)) { ?>
                    <img
                        src="view_file.php?id=<?= (int)$file['id'] ?>"
                        alt="Student uploaded image"
                        style="max-width:250px; max-height:250px">
                <?php } ?>

                <p>
                    <a href="view_file.php?id=<?= (int)$file['id'] ?>&download=1">
                        Open / Download File
                    </a>
                </p>
            </div>
        <?php } ?>
    <?php } else { ?>
        <p>No files uploaded for this student.</p>
    <?php } ?>

    <p><a href="index.php">Back to Student List</a></p>
</body>
</html>
<?php
$stmt->close();
$conn->close();
?>
