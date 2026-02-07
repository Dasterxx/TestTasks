/**
 * Статистика
 */

import { formatSize } from '../utils/formatters.js';
import { formatNumber } from '../utils/formatters.js';
import { loadStats as fetchStats } from '../services/api-service.js';

export function initStats(elements) {
    elements.refreshStatsBtn.addEventListener('click', () => loadStats(elements));
    loadStats(elements); // Загрузка при инициализации
}

export async function loadStats(elements) {
    try {
        const data = await fetchStats();

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