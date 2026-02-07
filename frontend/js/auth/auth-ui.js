import { showToast } from '../utils/toast.js';
import { saveAuthData, clearAuthData } from './auth-service.js'; // Добавляем clearAuthData

export function initAuthUI(authElements, showToastFn) {
    console.log('initAuthUI called');

    if (!authElements.loginBtn || !authElements.logoutBtn) {
        console.error('Auth buttons not found!');
        return;
    }

    authElements.loginBtn.addEventListener('click', () => login(authElements, showToastFn));
    authElements.logoutBtn.addEventListener('click', () => logout(authElements, showToastFn));

    const token = localStorage.getItem('fileExchange_token');
    if (token) {
        const username = localStorage.getItem('fileExchange_username');
        showLoggedIn(authElements, username);
    }
}

export async function login(authElements, showToastFn) {
    const username = authElements.usernameInput.value.trim();
    const password = authElements.passwordInput.value;

    if (!username) {
        showToastFn('Введите имя пользователя');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/login', { // Добавляем полный URL
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const data = await response.json();
        saveAuthData(data.token, data.userId, data.username);
        showLoggedIn(authElements, data.username);
        showToastFn(`Добро пожаловать, ${data.username}!`);

    } catch (err) {
        showToastFn('Ошибка входа: ' + err.message);
    }
}

export function logout(authElements, showToastFn) {
    clearAuthData();
    authElements.loginForm.hidden = false;
    authElements.userInfo.hidden = true;
    showToastFn('Вы вышли из системы');
}

export function showLoggedIn(authElements, username) {
    authElements.loginForm.hidden = true;
    authElements.userInfo.hidden = false;
    authElements.currentUser.textContent = username;
}