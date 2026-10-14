
const form = document.getElementById("registrationForm");
const errorMessage = document.getElementById("errorMessage");

form.addEventListener("submit", function(event) {

    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const roll = document.getElementById("roll").value.trim();
    const email = document.getElementById("email").value.trim();
    const course = document.getElementById("course").value;
    const password = document.getElementById("password").value;
    const file = document.getElementById("document").files[0];

    errorMessage.style.color = "red";

    if (!name || !roll || !email || !course || !password || !file) {
        errorMessage.textContent = "Please fill in all fields.";
        return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
        errorMessage.textContent = "Please enter a valid email.";
        return;
    }

    const passwordPattern =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

    if (!passwordPattern.test(password)) {
        errorMessage.textContent =
            "Password must have 8 characters, uppercase, lowercase, number and symbol.";
        return;
    }

    const allowedTypes = [
        "image/jpeg",
        "image/png",
        "application/pdf"
    ];

    if (!allowedTypes.includes(file.type)) {
        errorMessage.textContent = "Upload only JPG, PNG or PDF files.";
        return;
    }

    if (file.size > 2 * 1024 * 1024) {
        errorMessage.textContent = "File size must not exceed 2 MB.";
        return;
    }
    errorMessage.style.color = "green";
    errorMessage.textContent = "Registration form validated successfully!";

    console.log("Student Name:", name);
    console.log("Roll Number:", roll);
    console.log("Email:", email);
    console.log("Course:", course);
});

form.addEventListener("reset", function() {
    errorMessage.textContent = "";
});
