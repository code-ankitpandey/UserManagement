function registerUser() {
    document.getElementById("loadingOverlay").style.display = "block";
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword").value;

    validateEmail(emailInput);
    validatePassword(passwordInput);
    if (passwordInput.value !== confirmPassword) {
        showValidationMessage(passwordInput, "Passwords do not match.");
        hideLoadingOverlay();
        return;
    } else {
        hideValidationMessage(passwordInput);
    }

    const userData = {
        firstName: firstName,
        lastName: lastName,
        email: emailInput.value,
        password: password.value
    };

    fetch("http://localhost:8080/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    })
        .then((response) => response.text())
        .then((data) => {
            console.log("Registration response:", data);
            hideLoadingOverlay();
            openModal(data);
        })
        .catch((error) => {
            console.error("Registration error:", error);
            hideLoadingOverlay();
            openModal(error.message || "Registration failed");
        });
}

function changePassword() {
    document.getElementById("loadingOverlay").style.display = "block";
    const emailInput = document.getElementById("changePasswordEmail");
    const oldPassword = document.getElementById("changeOldPassword");
    const passwordInput = document.getElementById("changeNewPassword");
    const confirmPassword = document.getElementById("changeConfirmPassword");

    validateEmail(emailInput);
    validatePassword(passwordInput);

    if (passwordInput.value !== confirmPassword.value) {
        showValidationMessage(passwordInput, "Passwords do not match.");
        hideLoadingOverlay();
        return;
    } else {
        hideValidationMessage(passwordInput);
    }

    const userData = {
        email: emailInput.value,
        newPassword: passwordInput.value,
        oldPassword: oldPassword.value
    };

    fetch("http://localhost:8080/changePassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    })
        .then((response) => response.text())
        .then((data) => {
            console.log("Registration response:", data);
            hideLoadingOverlay();
            openModal(data);
        })
        .catch((error) => {
            console.error("Registration error:", error);
            hideLoadingOverlay();
            openModal(error.message || "Registration failed");
        });
}

function resetPassword() {
    document.getElementById("loadingOverlay").style.display = "block";
    const emailInput = document.getElementById("resetEmail");
    validateEmail(emailInput);
    const userData = {
        email: emailInput.value,
    };
    fetch("http://localhost:8080/resetPassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    })
        .then((response) => response.text())
        .then((data) => {
            console.log("Registration response:", data);
            hideLoadingOverlay();
            openModal(data);
        })
        .catch((error) => {
            console.error("Registration error:", error);
            hideLoadingOverlay();
            openModal(error.message || "Registration failed");
        });
}

function showValidationMessage(inputElement, message) {
    const validationMessage = inputElement.nextElementSibling;
    validationMessage.textContent = message;
    validationMessage.style.color = "red";
}

function hideValidationMessage(inputElement) {
    const validationMessage = inputElement.nextElementSibling;
    validationMessage.textContent = "";
}

function openModal(message) {
    let modal = document.getElementById("myModal");
    var modalText = document.getElementById("modalText");
    modalText.textContent = message;
    modal.style.display = "block";
}

function closeModal() {
    var modal = document.getElementById("myModal");
    modal.style.display = "none";
}

// Close the modal if clicked outside the content area
window.addEventListener("click", function (event) {
    var modal = document.getElementById("myModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
});

function hideLoadingOverlay() {
    document.getElementById("loadingOverlay").style.display = "none";
}

function savePassword() {
    var token = getQueryParam('token');
    var newPassword = document.getElementById('newPassword');
    validatePassword(newPassword);
    fetch('http://localhost:8080/savePassword?token=' + token, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({newPassword: newPassword.value}),
    })
        .then(response => response.text())
        .then(data => {
            document.getElementById('resetPasswordForm').style.display = 'none';
            document.getElementById('resultMessage').style.display = 'block';
            document.getElementById('resultMessage').innerText = data;
        })
        .catch(error => console.error('Error:', error));
}

function getQueryParam(param) {
    var urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

function validateEmail(
    emailInput
) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailInput.value)) {
        showValidationMessage(emailInput, "Invalid email. Please enter a valid email address.");
        hideLoadingOverlay();
        return;
    } else {
        hideValidationMessage(emailInput);
    }
}

function validatePassword(
    passwordInput
) {
    const password = passwordInput.value;
    const lengthCondition = password.length > 6;
    const uppercaseCondition = /[A-Z]/.test(password);
    const lowercaseCondition = /[a-z]/.test(password);
    const digitCondition = /\d/.test(password);

    if (!(lengthCondition && uppercaseCondition && lowercaseCondition && digitCondition)) {
        showValidationMessage(
            passwordInput,
            "Password must be at least 7 characters long, contain at least one uppercase letter, one lowercase letter, and one digit."
        );
        hideLoadingOverlay();
        return;
    } else {
        hideValidationMessage(passwordInput);
    }
}