import { initAuthUI } from './auth/auth-ui.js';
import { initUploadComponent, resetUpload, showProgress, finishProgress } from './components/upload.js';
import { initDownloadButton } from './components/download.js';
import { showResult } from './components/result.js';
import { initStats } from './components/stats.js';
import { uploadFile } from './services/api-service.js';
import { authState } from './auth/auth-service.js';
import { showToast } from './utils/toast.js';

const MAX_FILE_SIZE = 100 * 1024 * 1024;

const elements = {
    // Upload
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
    jsDownloadBtn: document.getElementById('jsDownloadBtn'),
    accessType: document.getElementById('accessType'),

    // Stats
    totalFiles: document.getElementById('totalFiles'),
    totalDownloads: document.getElementById('totalDownloads'),
    totalSize: document.getElementById('totalSize'),
    refreshStatsBtn: document.getElementById('refreshStatsBtn'),

    // Download
    downloadInput: document.getElementById('downloadInput'),
    downloadBtn: document.getElementById('downloadBtn'),

    // Toast
    toast: document.getElementById('toast')
};

// Auth elements
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

let isInitialized = false;

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    if (isInitialized) return;
    isInitialized = true;

    console.log('DOM loaded, initializing...');

    checkElements();

    initAuthUI(authElements, (msg) => showToast(msg, elements.toast));

    initUploadComponent({
        elements,
        maxFileSize: MAX_FILE_SIZE,
        onError: (msg) => showUploadError(msg),
        onSuccess: handleUploadSuccess
    });

    initDownloadButton(elements, authState, (msg) => showToast(msg, elements.toast));

    initStats(elements);
});

function checkElements() {
    const missing = [];
    for (const [key, el] of Object.entries(elements)) {
        if (!el) missing.push(key);
    }
    if (missing.length > 0) {
        console.error('Missing elements:', missing);
    } else {
        console.log('All elements found');
    }
}

async function handleUploadSuccess(file, isPublic) {
    showProgress(file.name, elements);

    try {
        const data = await uploadFile(file, isPublic);
        showResult(data, file.size, elements);
    } catch (err) {
        finishProgress(elements);
        throw err;
    }
}

function showUploadError(message) {
    elements.uploadArea.hidden = true;
    elements.progressContainer.hidden = true;
    elements.errorContainer.hidden = false;
    elements.errorMessage.textContent = message;
}