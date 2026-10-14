<?php
require 'db.php';
$result = $conn->query(
    "SELECT id, name, roll_number, email, course
     FROM students ORDER BY id DESC"
);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef2ff;
            padding: 20px;
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .card {
            background: white;
            padding: 22px;
            margin-bottom: 20px;
            border-radius: 10px;
        }

        input, select, button {
            box-sizing: border-box;
            width: 100%;
            padding: 10px;
            margin: 7px 0;
        }

        button {
            background: #4338ca;
            color: white;
            border: 0;
            cursor: pointer;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
            overflow-wrap: anywhere;
        }

        th {
            background: #4338ca;
            color: white;
        }

        a {
            color: #4338ca;
            margin-right: 7px;
        }

        .table-wrap {
            overflow-x: auto;
        }

        @media (max-width: 600px) {
            body { padding: 8px; }
            .card { padding: 12px; }
        }
    </style>
</head>
<body>
<div class="container">

    <div class="card">
        <h1>Student Management System</h1>
        <h2>Add New Student</h2>

        <form action="add_student.php" method="POST"
              enctype="multipart/form-data">

            <label>Student Name</label>
            <input type="text" name="name" maxlength="100"
                   required>

            <label>Roll Number</label>
            <input type="text" name="roll_number" maxlength="30"
                   required>

            <label>Email</label>
            <input type="email" name="email" maxlength="120"
                   required>

            <label>Course</label>
            <input type="text" name="course" maxlength="100"
                   required>

            <label>Profile Image / ID Proof / Certificate</label>
            <input type="file" name="document"
                   accept=".jpg,.jpeg,.png,.pdf">

            <button type="submit">Add Student</button>
        </form>
    </div>

    <div class="card">
        <h2>Student Records</h2>

        <div class="table-wrap">
        <table>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Roll Number</th>
                <th>Email</th>
                <th>Course</th>
                <th>Actions</th>
            </tr>

            <?php while ($row = $result->fetch_assoc()) { ?>
            <tr>
                <td><?= (int)$row['id'] ?></td>
                <td><?= htmlspecialchars($row['name']) ?></td>
                <td><?= htmlspecialchars($row['roll_number']) ?></td>
                <td><?= htmlspecialchars($row['email']) ?></td>
                <td><?= htmlspecialchars($row['course']) ?></td>

                <td>
                    <a href="view_student.php?id=<?= (int)$row['id'] ?>">
                        View
                    </a>

                    <a href="edit_student.php?id=<?= (int)$row['id'] ?>">
                        Edit
                    </a>

                    <form action="delete_student.php" method="POST"
                          onsubmit="return confirm('Delete this student?');"
                          style="display:inline">
                        <input type="hidden" name="id"
                               value="<?= (int)$row['id'] ?>">
                        <button type="submit" style="width:auto">
                            Delete
                        </button>
                    </form>
                </td>
            </tr>
            <?php } ?>
        </table>
        </div>
    </div>

</div>
</body>
</html>
<?php $conn->close(); ?>
