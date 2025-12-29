/**
 * htmx Configuration for Spring Security CSRF
 * This file must be loaded after htmx.js
 */

// Add CSRF token to all htmx requests
document.body.addEventListener('htmx:configRequest', function(event) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (csrfToken && csrfHeader) {
        event.detail.headers[csrfHeader] = csrfToken;
    }
});

// Handle authentication errors - redirect to login
document.body.addEventListener('htmx:responseError', function(event) {
    const status = event.detail.xhr.status;
    if (status === 401 || status === 403) {
        window.location.href = '/login?expired=true';
    } else if (status >= 500) {
        console.error('Server error:', event.detail.xhr.status, event.detail.xhr.responseText);
    }
});

// Handle swap errors
document.body.addEventListener('htmx:swapError', function(event) {
    console.error('htmx swap error:', event.detail);
});

// Handle timeout errors
document.body.addEventListener('htmx:timeout', function(event) {
    console.error('htmx timeout:', event.detail);
});

// Handle network errors
document.body.addEventListener('htmx:sendError', function(event) {
    console.error('htmx network error:', event.detail);
});
