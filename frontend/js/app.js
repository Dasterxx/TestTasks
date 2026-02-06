// Конфигурация
const API_BASE_URL = 'http://localhost:8080';
const MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB

// DOM элементы
const elements = {
    retryBtn: document.getElementById('retryBtn'),
    jsDownloadBtn: document.getElementById('jsDownloadBtn'),
    accessType: document.getElementById('accessType'),
    uploadArea: document.getElementById('uploadArea'),
    fileInput: document.getElementById('fileInput'),
    progressContainer: document.getElementById('progressContainer'),
    progressFill: document.getElementById('progressFill'),
    progressPercent: document.getElementById('progressPercent'),
    fileName: document.getElementById('fileName'),
    cancelBtn: document.getElementById('cancelBtn'),
    resultContainer: document.getElementById('resultContainer'),
    downloadLink: document.getElementById('downloadLink'),
    copyBtn: document.getElementById('copyBtn'),
    fileSize: document.getElementById('fileSize'),
    expiryDate: document.getElementById('expiryDate'),
    newUploadBtn: document.getElementById('newUploadBtn'),
    errorContainer: document.getElementById('errorContainer'),
    errorMessage: document.getElementById('errorMessage'),
    retryBtn: document.getElementById('retryBtn'),
    totalFiles: document.getElementById('totalFiles'),
    totalDownloads: document.getElementById('totalDownloads'),
    totalSize: document.getElementById('totalSize'),
    refreshStatsBtn: document.getElementById('refreshStatsBtn'),
    downloadInput: document.getElementById('downloadInput'),
    downloadBtn: document.getElementById('downloadBtn'),
    toast: document.getElementById('toast')
};
const state = {
    token: localStorage.getItem('fileExchange_token'),
    userId: localStorage.getItem('fileExchange_userId'),
    username: localStorage.getItem('fileExchange_username')
};

// DOM элементы для авторизации
const authElements = {
    authSection: document.getElementById('authSection'),
    loginForm: document.getElementById('loginForm'),
    userInfo: document.getElementById('userInfo'),
    currentUser: document.getElementById('currentUser'),
    usernameInput: document.getElementById('usernameInput'),
    passwordInput: document.getElementById('passwordInput'),
    loginBtn: document.getElementById('loginBtn'),
    logoutBtn: document.getElementById('logoutBtn')
};

let abortController = null;
let isInitialized = false;

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM loaded, initializing...');
    console.log('Token at startup:', state.token);
    if (isInitialized) return;
    isInitialized = true;

    initAuth();
    initUpload();
    initDownloadButton();  // Новая инициализация
    initStats();
    loadStats();
});


// ================== AUTHENTICATION =================
// Инициализация авторизации
function initAuth() {
    console.log('Auth init. Token:', state.token ? 'exists' : 'none');
    console.log('Username:', state.username);

    authElements.loginBtn.addEventListener('click', login);
    authElements.logoutBtn.addEventListener('click', logout);

    if (state.token) {
        showLoggedIn();
    }
}

async function login() {
    const username = authElements.usernameInput.value.trim();
    const password = authElements.passwordInput.value;

    if (!username) {
        showToast('Введите имя пользователя');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const data = await response.json();

        state.token = data.token;
        state.userId = data.userId;
        state.username = data.username;

        localStorage.setItem('fileExchange_token', data.token);
        localStorage.setItem('fileExchange_userId', data.userId);
        localStorage.setItem('fileExchange_username', data.username);

        showLoggedIn();
        showToast(`Добро пожаловать, ${data.username}!`);

    } catch (err) {
        showToast('Ошибка входа: ' + err.message);
    }
}

function logout() {
    state.token = null;
    state.userId = null;
    state.username = null;

    localStorage.removeItem('fileExchange_token');
    localStorage.removeItem('fileExchange_userId');
    localStorage.removeItem('fileExchange_username');

    authElements.loginForm.hidden = false;
    authElements.userInfo.hidden = true;
    showToast('Вы вышли из системы');
}

function showLoggedIn() {
    authElements.loginForm.hidden = true;
    authElements.userInfo.hidden = false;
    authElements.currentUser.textContent = state.username;
}

// Обновлённая функция upload с токеном
async function uploadFile(file, isPublic = false) {
    showProgress(file.name);

    const formData = new FormData();
    formData.append('file', file);

    const headers = {};
    if (state.token && !isPublic) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    abortController = new AbortController();

    try {
        const response = await fetch(`${API_BASE_URL}/upload`, {
            method: 'POST',
            headers,
            body: formData,
            signal: abortController.signal
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const data = await response.json();
        showResult(data, file.size);
        loadStats();

    } catch (err) {
        if (err.name === 'AbortError') {
            resetUpload();
        } else {
            showError(err.message || 'Ошибка загрузки файла');
        }
    }
}

// Обновлённая функция download с токеном
async function handleDownload() {
    let input = elements.downloadInput.value.trim();
    if (!input) {
        showToast('Введите ID файла или ссылку');
        return;
    }

    let fileId = input;
    if (input.includes('/download/')) {
        fileId = input.split('/download/').pop();
    }

    const headers = {};
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/download/${fileId}`, {
            headers
        });

        if (response.status === 403) {
            showToast('❌ Доступ запрещён. Это приватный файл.');
            return;
        }

        if (!response.ok) {
            throw new Error('Download failed');
        }

        // Скачиваем файл
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;

        // Извлекаем имя файла из Content-Disposition
        const disposition = response.headers.get('Content-Disposition');
        const filename = disposition ?
        disposition.split('filename=')[1].replace(/"/g, '') :
        'download';
        a.download = filename;

        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showToast('✅ Файл скачан!');
        loadStats();

    } catch (err) {
        showToast('Ошибка скачивания: ' + err.message);
    }
}
// ==================== UPLOAD ====================

// Исправленный initUpload в app.js

function initUpload() {
    if (!elements.uploadArea || !elements.fileInput) {
        console.error('Upload elements not found!');
        return;
    }
    // Удаляем старые обработчики если есть (на всякий случай)
    const newUploadArea = elements.uploadArea.cloneNode(true);
    elements.uploadArea.parentNode.replaceChild(newUploadArea, elements.uploadArea);
    elements.uploadArea = newUploadArea;

    const newFileInput = elements.fileInput.cloneNode(true);
    elements.fileInput.parentNode.replaceChild(newFileInput, elements.fileInput);
    elements.fileInput = newFileInput;

    // Теперь навешиваем обработчики на новые элементы
    elements.uploadArea.addEventListener('click', (e) => {
        // Не открываем если уже открыто
        if (elements.fileInput.disabled) return;
        elements.fileInput.click();
    });

    elements.fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Блокируем повторные клики на время обработки
        elements.fileInput.disabled = true;

        handleFile(file).finally(() => {
            elements.fileInput.disabled = false;
            e.target.value = '';  // Сброс для повторного выбора
        });
    });

    // Drag & Drop
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        elements.uploadArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    elements.uploadArea.addEventListener('dragenter', () => {
        elements.uploadArea.classList.add('dragover');
    });

    elements.uploadArea.addEventListener('dragleave', () => {
        elements.uploadArea.classList.remove('dragover');
    });

    elements.uploadArea.addEventListener('drop', (e) => {
        elements.uploadArea.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file) handleFile(file);
    });

    // Кнопки
    elements.cancelBtn.addEventListener('click', cancelUpload);
    elements.copyBtn.addEventListener('click', copyLink);
    elements.newUploadBtn.addEventListener('click', resetUpload);
    elements.retryBtn.addEventListener('click', resetUpload);
}

async function handleFile(file) {
    if (file.size > MAX_FILE_SIZE) {
        showError(`Файл слишком большой. Максимум: ${formatSize(MAX_FILE_SIZE)}`);
        return;
    }

    // Показываем диалог выбора режима (публичный/приватный)
    const isPublic = !state.token || confirm('Сделать файл публичным? (Отмена = приватный, только для вас)');

    await uploadFile(file, isPublic);
}

async function uploadFile(file, isPublic = false) {
    showProgress(file.name);

    const formData = new FormData();
    formData.append('file', file);

    const headers = {};
    // Отправляем токен только если файл приватный и есть токен
    if (state.token && !isPublic) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    abortController = new AbortController();

    try {
        console.log('Uploading file:', file.name, 'isPublic:', isPublic, 'hasToken:', !!state.token);

        const response = await fetch(`${API_BASE_URL}/upload`, {
            method: 'POST',
            headers,
            body: formData,
            signal: abortController.signal
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        const data = await response.json();
        console.log('Upload success:', data);

        showResult(data, file.size);
        loadStats();

    } catch (err) {
        console.error('Upload error:', err);
        if (err.name === 'AbortError') {
            resetUpload();
        } else {
            showError(err.message || 'Ошибка загрузки файла');
        }
    }
}

function showProgress(name) {
    elements.uploadArea.hidden = true;
    elements.progressContainer.hidden = false;
    elements.fileName.textContent = name;

    // Симуляция прогресса (т.к. fetch не даёт реальный прогресс FormData)
    let progress = 0;
    const interval = setInterval(() => {
        progress += Math.random() * 15;
        if (progress > 90) progress = 90;
        updateProgress(progress);
    }, 200);

    // Сохраняем interval чтобы очистить потом
    elements.progressContainer.dataset.interval = interval;
}

function updateProgress(percent) {
    elements.progressFill.style.width = `${percent}%`;
    elements.progressPercent.textContent = `${Math.round(percent)}%`;
}

function finishProgress() {
    clearInterval(elements.progressContainer.dataset.interval);
    updateProgress(100);
}

function cancelUpload() {
    if (abortController) {
        abortController.abort();
    }
}

function showResult(data, size) {
    console.log('showResult called');
    finishProgress();

    setTimeout(() => {
        elements.progressContainer.hidden = true;
        elements.resultContainer.hidden = false;

        const fullUrl = `${API_BASE_URL}${data.downloadUrl}`;
        const fileId = data.fileId;
        const isPublic = data.isPublic;

        // Находим кнопку ПОСЛЕ того как показали контейнер
        const btn = document.getElementById('jsDownloadBtn');
        if (!btn) {
            console.error('jsDownloadBtn not found in DOM!');
            return;
        }

        console.log('Found button:', btn);
        console.log('Setting fileId:', fileId);
        console.log('Current token:', state.token);

        // Сохраняем ID для скачивания
        btn.dataset.fileId = fileId;

        // Обновляем текст кнопки
        btn.textContent = isPublic
        ? '⬇️ Скачать файл (публичный)'
        : '⬇️ Скачать файл (приватный)';

        // Заполняем остальные поля
        elements.downloadLink.value = fullUrl;
        elements.fileSize.textContent = formatSize(size);
        elements.expiryDate.textContent = formatDate(
            new Date(Date.now() + data.expiresInDays * 24 * 60 * 60 * 1000)
        );

    }, 500);
}

// Инициализация кнопки скачивания один раз
function initDownloadButton() {
    console.log('initDownloadButton called');

    document.addEventListener('click', (e) => {
        console.log('Document click:', e.target.id);  // Отладка всех кликов

        if (e.target && e.target.id === 'jsDownloadBtn') {
            console.log('Download button clicked!');
            const fileId = e.target.dataset.fileId;
            console.log('fileId from dataset:', fileId);

            if (!fileId) {
                showToast('Ошибка: сначала загрузите файл');
                return;
            }
            downloadViaJs(fileId);
        }
    });

    console.log('Download button initialized via delegation');
}
async function downloadViaJs(fileId) {
    console.log('=== downloadViaJs called ===');

    // Берём токен ПРЯМО из localStorage, а не из state!
    const token = localStorage.getItem('fileExchange_token');
    console.log('Token from localStorage:', token ? 'exists' : 'none');

    const headers = {};

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
        console.log('Using token:', token.substring(0, 10) + '...');
    } else {
        console.log('No token available');
    }

    try {
        showToast('Начинаем скачивание...');

        const response = await fetch(`${API_BASE_URL}/download/${fileId}`, {
            headers
        });

        console.log('Response status:', response.status);

        if (response.status === 403) {
            showToast('❌ Доступ запрещён. Войдите в аккаунт владельца.');
            return;
        }

        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }

        const disposition = response.headers.get('Content-Disposition');
        let filename = 'download';
        if (disposition) {
            const match = disposition.match(/filename="([^"]*)"/);
            if (match) filename = match[1];
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        window.URL.revokeObjectURL(url);
        showToast('✅ Файл скачан!');
        loadStats();

    } catch (err) {
        console.error('Download error:', err);
        showToast('❌ Ошибка скачивания: ' + err.message);
    }
}

function showError(message) {
    elements.uploadArea.hidden = true;
    elements.progressContainer.hidden = true;
    elements.errorContainer.hidden = false;
    elements.errorMessage.textContent = message;
}

function resetUpload() {
    elements.fileInput.value = '';
    elements.uploadArea.hidden = false;
    elements.progressContainer.hidden = true;
    elements.resultContainer.hidden = true;
    elements.errorContainer.hidden = true;
    updateProgress(0);
}

function copyLink() {
    elements.downloadLink.select();
    document.execCommand('copy');

    elements.copyBtn.textContent = '✓ Скопировано!';
    elements.copyBtn.classList.add('copied');

    showToast('Ссылка скопирована в буфер обмена');

    setTimeout(() => {
        elements.copyBtn.textContent = '📋 Копировать';
        elements.copyBtn.classList.remove('copied');
    }, 2000);
}

// ==================== DOWNLOAD ====================

function initUpload() {
    console.log('initUpload called');

    // Проверяем элементы
    if (!elements.uploadArea || !elements.fileInput) {
        console.error('Upload elements not found!');
        return;
    }

    // НЕ используем cloneNode - просто навешиваем обработчики
    // Убираем старые обработчики через замену функций

    elements.uploadArea.onclick = (e) => {
        if (elements.fileInput.disabled) return;
        elements.fileInput.click();
    };

    elements.fileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        elements.fileInput.disabled = true;
        handleFile(file).finally(() => {
            elements.fileInput.disabled = false;
            e.target.value = '';
        });
    };

    // Drag & Drop
    elements.uploadArea.ondragenter = (e) => {
        e.preventDefault();
        elements.uploadArea.classList.add('dragover');
    };

    elements.uploadArea.ondragleave = (e) => {
        e.preventDefault();
        elements.uploadArea.classList.remove('dragover');
    };

    elements.uploadArea.ondragover = (e) => {
        e.preventDefault();
    };

    elements.uploadArea.ondrop = (e) => {
        e.preventDefault();
        elements.uploadArea.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file) handleFile(file);
    };

    // Кнопки
    elements.cancelBtn.onclick = cancelUpload;
    elements.copyBtn.onclick = copyLink;
    elements.newUploadBtn.onclick = resetUpload;
    if (elements.retryBtn) {
        elements.retryBtn.onclick = resetUpload;
    }

    console.log('initUpload completed');
}

async function handleDownload() {
    let input = elements.downloadInput.value.trim();
    if (!input) {
        showToast('Введите ID файла или ссылку');
        return;
    }

    let fileId = input;
    if (input.includes('/download/')) {
        fileId = input.split('/download/').pop();
    }

    // Убираем всё после ? или #
    fileId = fileId.split('?')[0].split('#')[0];

    const headers = {};
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
        console.log('Using token for download:', state.token.substring(0, 10) + '...');
    } else {
        console.log('No token, anonymous download');
    }

    try {
        showToast('Загрузка файла...');

        const response = await fetch(`${API_BASE_URL}/download/${fileId}`, {
            headers
        });

        console.log('Download response status:', response.status);

        if (response.status === 403) {
            showToast('❌ Доступ запрещён. Это приватный файл. Войдите в аккаунт владельца.');
            return;
        }

        if (response.status === 404) {
            showToast('❌ Файл не найден или истёк срок хранения');
            return;
        }

        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }

        const disposition = response.headers.get('Content-Disposition');
        let filename = 'download';
        if (disposition) {
            const match = disposition.match(/filename="([^"]*)"/);
            if (match) filename = match[1];
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        window.URL.revokeObjectURL(url);
        showToast('✅ Файл скачан!');
        loadStats();

    } catch (err) {
        console.error('Download error:', err);
        showToast('❌ Ошибка: ' + err.message);
    }
}

// ==================== STATISTICS ====================

function initStats() {
    elements.refreshStatsBtn.addEventListener('click', loadStats);
}

async function loadStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats`);
        if (!response.ok) throw new Error('Failed to load stats');

        const data = await response.json();

        elements.totalFiles.textContent = formatNumber(data.totalFiles || 0);
        elements.totalDownloads.textContent = formatNumber(data.totalDownloads || 0);
        elements.totalSize.textContent = formatSize(data.totalBytesUploaded || 0);

    } catch (err) {
        console.error('Failed to load stats:', err);
        elements.totalFiles.textContent = '-';
        elements.totalDownloads.textContent = '-';
        elements.totalSize.textContent = '-';
    }
}

// ==================== UTILS ====================

function formatSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatNumber(num) {
    return num.toLocaleString('ru-RU');
}

function formatDate(date) {
    return date.toLocaleDateString('ru-RU', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
}

function showToast(message) {
    elements.toast.textContent = message;
    elements.toast.hidden = false;
    elements.toast.classList.remove('hidden');

    setTimeout(() => {
        elements.toast.classList.add('hidden');
        setTimeout(() => {
            elements.toast.hidden = true;
        }, 300);
    }, 3000);
}