/**
 * Система уведомлений
 */

export function showToast(message, toastElement) {
    toastElement.textContent = message;
    toastElement.hidden = false;
    toastElement.classList.remove('hidden');

    setTimeout(() => {
        toastElement.classList.add('hidden');
        setTimeout(() => {
            toastElement.hidden = true;
        }, 300);
    }, 3000);
}