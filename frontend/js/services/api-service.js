const API_BASE_URL = 'http://localhost:8080';

export async function uploadFile(file, isPublic = false) {
    const formData = new FormData();
    formData.append('file', file);

    const headers = {};
    if (!isPublic) {
        const token = localStorage.getItem('fileExchange_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    }

    try {
        const response = await fetch(`${API_BASE_URL}/upload`, {
            method: 'POST',
            headers,
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();

            // Обработка специфических ошибок
            if (response.status === 403) {
                // Ошибка типа файла
                throw new Error(`❌ ${extractErrorMessage(errorText)}`);
            }

            throw new Error(errorText);
        }

        return await response.json();

    } catch (err) {
        throw new Error(err.message || 'Ошибка загрузки файла');
    }
}
function extractErrorMessage(htmlOrText) {
    // Если пришёл JSON с ошибкой
    try {
        const json = JSON.parse(htmlOrText);
        return json.error || htmlOrText;
    } catch {
        return htmlOrText.replace(/[{}"]/g, '').replace(/error:/, '').trim();
    }
}
export async function login(username, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        return await response.json();

    } catch (err) {
        throw new Error('Ошибка входа: ' + err.message);
    }
}

export async function downloadFile(fileId, token) {
    const headers = {};
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}/download/${fileId}`, { headers });

    if (!response.ok) {
        if (response.status === 403) {
            throw new Error('Access denied');
        }
        throw new Error('Download failed: ' + response.status);
    }

    return response;
}

export async function loadStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats`);
        if (!response.ok) throw new Error('Failed to load stats');
        return await response.json();
    } catch (err) {
        console.error('Failed to load stats:', err);
        throw err;
    }
}