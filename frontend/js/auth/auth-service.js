export const authState = {
    token: localStorage.getItem('fileExchange_token'),
    userId: localStorage.getItem('fileExchange_userId'),
    username: localStorage.getItem('fileExchange_username')
};

export function saveAuthData(token, userId, username) {
    authState.token = token;
    authState.userId = userId;
    authState.username = username;

    localStorage.setItem('fileExchange_token', token);
    localStorage.setItem('fileExchange_userId', userId);
    localStorage.setItem('fileExchange_username', username);
}

export function clearAuthData() {
    authState.token = null;
    authState.userId = null;
    authState.username = null;

    localStorage.removeItem('fileExchange_token');
    localStorage.removeItem('fileExchange_userId');
    localStorage.removeItem('fileExchange_username');
}