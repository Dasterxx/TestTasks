import { formatSize } from '../utils/formatters.js';
import { showToast } from '../utils/toast.js';

export function initUploadComponent(config) {
    const { elements, maxFileSize, onError, onSuccess, onProgress } = config;

    if (!elements.uploadArea || !elements.fileInput) {
        console.error('Upload elements not found!');
        return;
    }

    elements.uploadArea.addEventListener('click', () => {
        if (elements.fileInput.disabled) return;
        elements.fileInput.click();
    });

    elements.fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;

        elements.fileInput.disabled = true;
        handleFile(file, maxFileSize, elements, onError, onSuccess, onProgress).finally(() => {
            elements.fileInput.disabled = false;
            e.target.value = '';
        });
    });

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        elements.uploadArea.addEventListener(eventName, (e) => {
            e.preventDefault();
            e.stopPropagation();
        });
    });

    elements.uploadArea.addEventListener('dragenter', () => {
        elements.uploadArea.classList.add('dragover');
    });

    elements.uploadArea.addEventListener('dragleave', () => {
        elements.uploadArea.classList.remove('dragover');
    });

    elements.uploadArea.addEventListener('drop', (e) => {
        elements.uploadArea.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file) handleFile(file, maxFileSize, elements, onError, onSuccess, onProgress);
    });

    // Кнопки
    elements.cancelBtn.addEventListener('click', () => cancelUpload(elements));
    elements.copyBtn.addEventListener('click', () => copyLink(elements));
    elements.newUploadBtn.addEventListener('click', () => resetUpload(elements));
    if (elements.retryBtn) {
        elements.retryBtn.addEventListener('click', () => resetUpload(elements));
    }
}

export async function handleFile(file, maxFileSize, elements, onError, onSuccess, onProgress) {
    if (file.size > maxFileSize) {
        onError(`Файл слишком большой. Максимум: ${formatSize(maxFileSize)}`);
        return;
    }

    showProgress(file.name, elements);

    // Спрашиваем публичный/приватный
    const isPublic = confirm('Сделать файл публичным? (Отмена = приватный, только для вас)');

    try {
        const data = await onSuccess(file, isPublic);
        return data;
    } catch (err) {
        if (err.name !== 'AbortError') {
            onError(err.message);
        }
        throw err;
    }
}

export function showProgress(name, elements) {
    elements.uploadArea.hidden = true;
    elements.progressContainer.hidden = false;
    elements.fileName.textContent = name;

    let progress = 0;
    const interval = setInterval(() => {
        progress += Math.random() * 15;
        if (progress > 90) progress = 90;
        updateProgress(progress, elements);
    }, 200);

    elements.progressContainer.dataset.interval = interval;
}

export function updateProgress(percent, elements) {
    elements.progressFill.style.width = `${percent}%`;
    elements.progressPercent.textContent = `${Math.round(percent)}%`;
}

export function finishProgress(elements) {
    if (elements.progressContainer.dataset.interval) {
        clearInterval(elements.progressContainer.dataset.interval);
    }
    updateProgress(100, elements);
}

export function cancelUpload(elements) {
    resetUpload(elements);
}

export function resetUpload(elements) {
    elements.fileInput.value = '';
    elements.uploadArea.hidden = false;
    elements.progressContainer.hidden = true;
    elements.resultContainer.hidden = true;
    elements.errorContainer.hidden = true;
    updateProgress(0, elements);
}

export function copyLink(elements) {
    elements.downloadLink.select();
    document.execCommand('copy');

    elements.copyBtn.textContent = '✓ Скопировано!';
    elements.copyBtn.classList.add('copied');

    showToast('Ссылка скопирована в буфер обмена', elements.toast);

    setTimeout(() => {
        elements.copyBtn.textContent = '📋 Копировать';
        elements.copyBtn.classList.remove('copied');
    }, 2000);
}