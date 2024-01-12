export const setCookie = (name, value, days) => {
    const date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    const expires = `expires=${date.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/;`;
};
export const getCookie = (name) => {
    const cookies = document.cookie.split(';');
    const cookie = cookies.find((cookie) => cookie.trim().startsWith(`${name}=`));

    if (cookie) {
        return cookie.split('=')[1];
    } else {
        return null;
    }
};
export const deleteCookie = (name) => {
    document.cookie = `${name}=; expires=Thu, 08 Feb 1999 00:00:00 UTC; path=/;`;
};


