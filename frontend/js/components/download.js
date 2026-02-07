/**
 * Компонент скачивания файла
 */

import { showToast } from '../utils/toast.js';

export function initDownloadButton(elements, state, showToastFn) {
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'jsDownloadBtn') {
            const fileId = e.target.dataset.fileId;
            if (!fileId) {
                showToastFn('Ошибка: сначала загрузите файл');
                return;
            }
            downloadViaJs(fileId, showToastFn);
        }
    });

    // Инициализация кнопки скачивания по ссылке
    if (elements.downloadBtn) {
        elements.downloadBtn.addEventListener('click', () => {
            handleDownload(elements, state, showToastFn);
        });
    }
}

export async function downloadViaJs(fileId, showToastFn) {
    const token = localStorage.getItem('fileExchange_token');
    const headers = {};

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        showToastFn('Начинаем скачивание...');

        const response = await fetch(`http://localhost:8080/download/${fileId}`, {
            headers
        });

        if (response.status === 403) {
            showToastFn('❌ Доступ запрещён. Войдите в аккаунт владельца.');
            return;
        }

        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }

        const disposition = response.headers.get('Content-Disposition');
        let filename = 'download';
        if (disposition) {
            const match = disposition.match(/filename="([^\"]*)"/);
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
        showToastFn('✅ Файл скачан!');

    } catch (err) {
        console.error('Download error:', err);
        showToastFn('❌ Ошибка скачивания: ' + err.message);
    }
}

export async function handleDownload(elements, state, showToastFn) {
    let input = elements.downloadInput.value.trim();
    if (!input) {
        showToastFn('Введите ID файла или ссылку');
        return;
    }

    let fileId = input;
    if (input.includes('/download/')) {
        fileId = input.split('/download/').pop();
    }
    fileId = fileId.split('?')[0].split('#')[0];

    const headers = {};
    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        showToastFn('Загрузка файла...');

        const response = await fetch(`http://localhost:8080/download/${fileId}`, {
            headers
        });

        if (response.status === 403) {
            showToastFn('❌ Доступ запрещён. Это приватный файл. Войдите в аккаунт владельца.');
            return;
        }

        if (response.status === 404) {
            showToastFn('❌ Файл не найден или истёк срок хранения');
            return;
        }

        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }

        const disposition = response.headers.get('Content-Disposition');
        let filename = 'download';
        if (disposition) {
            const match = disposition.match(/filename="([^\"]*)"/);
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
        showToastFn('✅ Файл скачан!');

    } catch (err) {
        console.error('Download error:', err);
        showToastFn('❌ Ошибка: ' + err.message);
    }
}