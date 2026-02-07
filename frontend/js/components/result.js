import { formatSize, formatDate } from '../utils/formatters.js';
import { finishProgress } from './upload.js';

export function showResult(data, size, elements) {
    finishProgress(elements);

    setTimeout(() => {
        elements.progressContainer.hidden = true;
        elements.resultContainer.hidden = false;

        const fullUrl = `http://localhost:8080${data.downloadUrl}`;
        const fileId = data.fileId;
        const isPublic = data.isPublic;

        const btn = document.getElementById('jsDownloadBtn');
        if (!btn) {
            console.error('jsDownloadBtn not found in DOM!');
            return;
        }

        btn.dataset.fileId = fileId;
        btn.textContent = isPublic
        ? '⬇️ Скачать файл (публичный)'
        : '⬇️ Скачать файл (приватный)';

        // Показываем тип доступа
        if (elements.accessType) {
            elements.accessType.textContent = isPublic
            ? '🔓 Доступ: публичный'
            : '🔒 Доступ: только для вас';
        }

        elements.downloadLink.value = fullUrl;
        elements.fileSize.textContent = formatSize(size);
        elements.expiryDate.textContent = formatDate(
            new Date(Date.now() + data.expiresInDays * 24 * 60 * 60 * 1000)
        );

    }, 500);
}