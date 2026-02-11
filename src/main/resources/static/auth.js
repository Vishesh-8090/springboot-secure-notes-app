const apiAuthUrl = '/auth';

const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const loginError = document.getElementById('login-error');
const registerError = document.getElementById('register-error');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${apiAuthUrl}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Success - Redirect to home
            window.location.href = '/';
        } else {
            showError(loginError, data.message || 'Login failed. Please check your credentials.');
        }
    } catch (error) {
        showError(loginError, 'An error occurred. Please try again later.');
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const response = await fetch(`${apiAuthUrl}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Success - Auto login or show success message
            // For now, let's just switch to login
            alert('Registration successful! Please sign in.');
            document.getElementById('show-login').click();
        } else {
            showError(registerError, data.message || 'Registration failed.');
        }
    } catch (error) {
        showError(registerError, 'An error occurred. Please try again later.');
    }
});

function showError(element, message) {
    element.textContent = message;
    element.style.display = 'block';
    setTimeout(() => {
        element.style.display = 'none';
    }, 5000);
}
