# htmx + Alpine.js CSP Implementation Plan

## Overview

This document outlines the migration strategy from vanilla JavaScript to htmx and Alpine.js CSP build for the Sahabat Quran web application.

**Current State:**
- 76 Thymeleaf templates
- Vanilla JavaScript with fetch API for AJAX
- Alpine.js 3.x (standard build) for dropdowns only
- No htmx usage
- Manual modal management
- Manual form submissions

**Target State:**
- htmx 2.x for server interactions (AJAX, partial updates, polling)
- Alpine.js CSP build 3.15.2 for client-side interactivity
- Declarative HTML attributes replacing imperative JavaScript
- CSP-compliant for DAST security compliance

---

## Phase 1: Foundation Setup

### 1.1 Update Layout Templates

**File: `src/main/resources/templates/layouts/main.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- CSRF Meta Tags for htmx -->
    <meta name="_csrf" th:content="${_csrf.token}"/>
    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>

    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"/>

    <!-- htmx 2.x -->
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>

    <!-- Alpine.js CSP Build -->
    <script defer src="https://cdn.jsdelivr.net/npm/@alpinejs/csp@3.15.2/dist/cdn.min.js"></script>

    <title layout:title-pattern="$CONTENT_TITLE - Sahabat Quran">Sahabat Quran</title>
</head>
<body hx-headers='{"X-CSRF-TOKEN": "[[${_csrf.token}]]"}'>
    <!-- Content -->
    <div layout:fragment="content"></div>

    <!-- Alpine.js CSP Component Registration -->
    <script layout:fragment="alpine-components"></script>

    <!-- Additional Scripts -->
    <script layout:fragment="scripts"></script>
</body>
</html>
```

### 1.2 Create htmx Configuration

**File: `src/main/resources/static/js/htmx-config.js`**

```javascript
// htmx Configuration for Spring Security CSRF
document.body.addEventListener('htmx:configRequest', function(event) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (csrfToken && csrfHeader) {
        event.detail.headers[csrfHeader] = csrfToken;
    }
});

// Handle htmx errors globally
document.body.addEventListener('htmx:responseError', function(event) {
    const status = event.detail.xhr.status;
    if (status === 403) {
        window.location.href = '/login?expired=true';
    } else if (status === 500) {
        console.error('Server error:', event.detail.xhr.responseText);
    }
});

// Handle swap errors
document.body.addEventListener('htmx:swapError', function(event) {
    console.error('Swap error:', event.detail);
});
```

### 1.3 Create Alpine.js CSP Components Registry

**File: `src/main/resources/static/js/alpine-components.js`**

```javascript
// Alpine.js CSP requires explicit component registration
// All x-data components must be registered here

document.addEventListener('alpine:init', () => {

    // Dropdown Component
    Alpine.data('dropdown', () => ({
        open: false,
        toggle() {
            this.open = !this.open;
        },
        close() {
            this.open = false;
        }
    }));

    // Mobile Menu Component
    Alpine.data('mobileMenu', () => ({
        open: false,
        toggle() {
            this.open = !this.open;
        }
    }));

    // Modal Component
    Alpine.data('modal', (initialOpen = false) => ({
        open: initialOpen,
        show() {
            this.open = true;
            document.body.style.overflow = 'hidden';
        },
        hide() {
            this.open = false;
            document.body.style.overflow = 'auto';
        }
    }));

    // Confirmation Dialog Component
    Alpine.data('confirmDialog', () => ({
        open: false,
        title: '',
        message: '',
        confirmAction: null,
        show(title, message, action) {
            this.title = title;
            this.message = message;
            this.confirmAction = action;
            this.open = true;
        },
        confirm() {
            if (this.confirmAction) {
                this.confirmAction();
            }
            this.open = false;
        },
        cancel() {
            this.open = false;
            this.confirmAction = null;
        }
    }));

    // Form Validation Component
    Alpine.data('formValidation', () => ({
        errors: {},
        hasError(field) {
            return this.errors[field] !== undefined;
        },
        getError(field) {
            return this.errors[field] || '';
        },
        setError(field, message) {
            this.errors[field] = message;
        },
        clearError(field) {
            delete this.errors[field];
        },
        clearAllErrors() {
            this.errors = {};
        }
    }));

    // Star Rating Component
    Alpine.data('starRating', (initialValue = 0) => ({
        rating: initialValue,
        hoverRating: 0,
        setRating(value) {
            this.rating = value;
        },
        setHover(value) {
            this.hoverRating = value;
        },
        clearHover() {
            this.hoverRating = 0;
        },
        isActive(star) {
            return star <= (this.hoverRating || this.rating);
        }
    }));

    // Character Counter Component
    Alpine.data('charCounter', (maxLength = 500) => ({
        count: 0,
        max: maxLength,
        update(value) {
            this.count = value.length;
        },
        remaining() {
            return this.max - this.count;
        },
        isOverLimit() {
            return this.count > this.max;
        }
    }));

    // Tab Navigation Component
    Alpine.data('tabs', (initialTab = 0) => ({
        activeTab: initialTab,
        setTab(index) {
            this.activeTab = index;
        },
        isActive(index) {
            return this.activeTab === index;
        }
    }));

    // Auto-save Component
    Alpine.data('autoSave', (saveUrl, debounceMs = 3000) => ({
        status: 'idle', // idle, saving, saved, error
        timer: null,
        triggerSave(data) {
            clearTimeout(this.timer);
            this.status = 'saving';
            this.timer = setTimeout(() => {
                this.performSave(saveUrl, data);
            }, debounceMs);
        },
        async performSave(url, data) {
            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(data)
                });
                this.status = response.ok ? 'saved' : 'error';
            } catch (e) {
                this.status = 'error';
            }
        }
    }));

    // Session Timer Component
    Alpine.data('sessionTimer', (startTime) => ({
        elapsed: 0,
        interval: null,
        start() {
            const start = new Date(startTime).getTime();
            this.interval = setInterval(() => {
                this.elapsed = Math.floor((Date.now() - start) / 1000);
            }, 1000);
        },
        stop() {
            clearInterval(this.interval);
        },
        formatted() {
            const hours = Math.floor(this.elapsed / 3600);
            const minutes = Math.floor((this.elapsed % 3600) / 60);
            const seconds = this.elapsed % 60;
            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        }
    }));

    // Polling Component (for real-time updates)
    Alpine.data('polling', (url, intervalMs = 30000) => ({
        data: null,
        error: null,
        interval: null,
        async fetch() {
            try {
                const response = await fetch(url);
                this.data = await response.json();
                this.error = null;
            } catch (e) {
                this.error = e.message;
            }
        },
        start() {
            this.fetch();
            this.interval = setInterval(() => this.fetch(), intervalMs);
        },
        stop() {
            clearInterval(this.interval);
        }
    }));
});
```

### 1.4 Content Security Policy Configuration

**File: `src/main/java/com/sahabatquran/webapp/config/SecurityConfig.java`**

Add CSP headers:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // ... existing configuration ...
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' https://unpkg.com https://cdn.jsdelivr.net https://cdn.tailwindcss.com; " +
                    "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://cdn.tailwindcss.com; " +
                    "font-src 'self' https://cdnjs.cloudflare.com; " +
                    "img-src 'self' data: https:; " +
                    "connect-src 'self';"
                )
            )
        );
    return http.build();
}
```

---

## Phase 2: Navigation & Layout Migration

### 2.1 Navigation Dropdowns

**Before (inline Alpine.js):**
```html
<div x-data="{ open: false }">
    <button @click="open = !open">Menu</button>
    <div x-show="open" @click.away="open = false">...</div>
</div>
```

**After (Alpine.js CSP):**
```html
<div x-data="dropdown">
    <button x-on:click="toggle">Menu</button>
    <div x-show="open" x-on:click.away="close"
         x-transition:enter="transition ease-out duration-100"
         x-transition:enter-start="opacity-0 scale-95"
         x-transition:enter-end="opacity-100 scale-100"
         x-transition:leave="transition ease-in duration-75"
         x-transition:leave-start="opacity-100 scale-100"
         x-transition:leave-end="opacity-0 scale-95">
        ...
    </div>
</div>
```

### 2.2 Mobile Menu

**File: `src/main/resources/templates/fragments/navigation.html`**

```html
<nav x-data="mobileMenu" class="bg-white shadow">
    <!-- Desktop Navigation -->
    <div class="hidden md:flex">
        <!-- Menu items -->
    </div>

    <!-- Mobile Menu Button -->
    <button x-on:click="toggle" class="md:hidden">
        <i class="fas fa-bars"></i>
    </button>

    <!-- Mobile Menu Panel -->
    <div x-show="open"
         x-transition:enter="transition ease-out duration-200"
         x-transition:enter-start="opacity-0 -translate-y-1"
         x-transition:enter-end="opacity-100 translate-y-0"
         class="md:hidden">
        <!-- Mobile menu items -->
    </div>
</nav>
```

---

## Phase 3: Modal Migration

### 3.1 Standard Modal Component

**Create Thymeleaf fragment: `src/main/resources/templates/fragments/modals.html`**

```html
<!-- Reusable Modal Fragment -->
<th:block th:fragment="modal(id, title)">
    <div th:id="${id}" x-data="modal" x-show="open"
         class="fixed inset-0 z-50 overflow-y-auto"
         style="display: none;">

        <!-- Backdrop -->
        <div x-show="open"
             x-transition:enter="transition ease-out duration-300"
             x-transition:enter-start="opacity-0"
             x-transition:enter-end="opacity-100"
             x-transition:leave="transition ease-in duration-200"
             x-transition:leave-start="opacity-100"
             x-transition:leave-end="opacity-0"
             class="fixed inset-0 bg-gray-600 bg-opacity-50"
             x-on:click="hide">
        </div>

        <!-- Modal Panel -->
        <div x-show="open"
             x-transition:enter="transition ease-out duration-300"
             x-transition:enter-start="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
             x-transition:enter-end="opacity-100 translate-y-0 sm:scale-100"
             x-transition:leave="transition ease-in duration-200"
             x-transition:leave-start="opacity-100 translate-y-0 sm:scale-100"
             x-transition:leave-end="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
             class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">

            <!-- Header -->
            <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-medium" th:text="${title}">Modal Title</h3>
                <button x-on:click="hide" class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-times"></i>
                </button>
            </div>

            <!-- Content Slot -->
            <div th:replace="${content}"></div>
        </div>
    </div>
</th:block>
```

### 3.2 Modal with htmx Content Loading

```html
<!-- Modal that loads content via htmx -->
<div x-data="modal" x-show="open" class="fixed inset-0 z-50">
    <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div id="modal-content"
             hx-get="/api/modal-content"
             hx-trigger="load"
             hx-swap="innerHTML">
            <div class="animate-pulse">Loading...</div>
        </div>
    </div>
</div>
```

---

## Phase 4: Form Migration

### 4.1 Standard Form with htmx Submit

**Before:**
```html
<form id="myForm" th:action="@{/submit}" method="post">
    <input type="text" name="field1">
    <button type="submit">Submit</button>
</form>
<script>
document.getElementById('myForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const response = await fetch(e.target.action, {
        method: 'POST',
        body: new FormData(e.target)
    });
    // Handle response
});
</script>
```

**After:**
```html
<form hx-post="/submit"
      hx-target="#result"
      hx-swap="innerHTML"
      hx-indicator="#loading">
    <input type="text" name="field1">
    <button type="submit">
        <span id="loading" class="htmx-indicator">
            <i class="fas fa-spinner fa-spin"></i>
        </span>
        Submit
    </button>
</form>
<div id="result"></div>
```

### 4.2 Form with Validation Feedback

```html
<form x-data="formValidation"
      hx-post="/api/register"
      hx-target="#form-result"
      hx-swap="innerHTML"
      hx-on::after-request="clearAllErrors()">

    <div class="mb-4">
        <label>Email</label>
        <input type="email" name="email"
               x-bind:class="{ 'border-red-500': hasError('email') }"
               hx-post="/api/validate/email"
               hx-trigger="blur changed"
               hx-target="next .error-message"
               hx-swap="innerHTML">
        <span class="error-message text-red-500 text-sm"></span>
    </div>

    <button type="submit">Register</button>
</form>
```

### 4.3 Multi-Step Form with Tabs

```html
<div x-data="tabs(0)">
    <!-- Tab Headers -->
    <div class="flex border-b">
        <button x-on:click="setTab(0)"
                x-bind:class="{ 'border-blue-500 text-blue-600': isActive(0) }"
                class="px-4 py-2 border-b-2">
            Step 1: Personal Info
        </button>
        <button x-on:click="setTab(1)"
                x-bind:class="{ 'border-blue-500 text-blue-600': isActive(1) }"
                class="px-4 py-2 border-b-2">
            Step 2: Education
        </button>
        <button x-on:click="setTab(2)"
                x-bind:class="{ 'border-blue-500 text-blue-600': isActive(2) }"
                class="px-4 py-2 border-b-2">
            Step 3: Program
        </button>
    </div>

    <!-- Tab Panels -->
    <form hx-post="/api/register" hx-target="#result">
        <div x-show="isActive(0)">
            <!-- Personal info fields -->
        </div>
        <div x-show="isActive(1)">
            <!-- Education fields -->
        </div>
        <div x-show="isActive(2)">
            <!-- Program fields -->
            <button type="submit">Complete Registration</button>
        </div>
    </form>
</div>
```

---

## Phase 5: Table & List Migration

### 5.1 Searchable Table with htmx

**Before (full page reload):**
```html
<form th:action="@{/registrations}" method="get">
    <input name="fullName" placeholder="Search">
    <button type="submit">Search</button>
</form>
<table>
    <tr th:each="item : ${items}">...</tr>
</table>
```

**After (partial update with htmx):**
```html
<form hx-get="/registrations"
      hx-target="#table-container"
      hx-swap="innerHTML"
      hx-push-url="true"
      hx-trigger="submit, keyup changed delay:500ms from:#search-input">
    <input id="search-input" name="fullName" placeholder="Search">
    <select name="status"
            hx-get="/registrations"
            hx-target="#table-container"
            hx-trigger="change">
        <option value="">All Statuses</option>
        <option th:each="s : ${statusOptions}" th:value="${s}" th:text="${s}">Status</option>
    </select>
</form>

<div id="table-container">
    <th:block th:replace="~{registrations/fragments :: table-rows}"></th:block>
</div>
```

**Create fragment: `src/main/resources/templates/registrations/fragments.html`**

```html
<th:block th:fragment="table-rows">
    <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
            <tr th:each="item : ${items}" th:id="'row-' + ${item.id}">
                <td th:text="${item.fullName}">Name</td>
                <td th:text="${item.email}">Email</td>
                <td>
                    <span th:text="${item.status}"
                          th:class="${item.status == 'APPROVED' ? 'text-green-600' : 'text-yellow-600'}">
                        Status
                    </span>
                </td>
                <td>
                    <button hx-get="@{'/registrations/' + ${item.id}}"
                            hx-target="#detail-modal-content"
                            hx-swap="innerHTML"
                            x-on:htmx:after-swap="$dispatch('open-modal')">
                        View
                    </button>
                </td>
            </tr>
        </tbody>
    </table>

    <!-- Pagination -->
    <div class="mt-4 flex justify-between">
        <button th:if="${page.hasPrevious()}"
                hx-get="@{/registrations(page=${page.number - 1})}"
                hx-target="#table-container">
            Previous
        </button>
        <span th:text="'Page ' + ${page.number + 1} + ' of ' + ${page.totalPages}"></span>
        <button th:if="${page.hasNext()}"
                hx-get="@{/registrations(page=${page.number + 1})}"
                hx-target="#table-container">
            Next
        </button>
    </div>
</th:block>
```

### 5.2 Inline Row Editing

```html
<tr th:id="'row-' + ${item.id}">
    <td th:text="${item.fullName}">Name</td>
    <td th:text="${item.email}">Email</td>
    <td>
        <button hx-get="@{'/registrations/' + ${item.id} + '/edit'}"
                hx-target="@{'#row-' + ${item.id}}"
                hx-swap="outerHTML">
            Edit
        </button>
    </td>
</tr>

<!-- Edit row fragment -->
<tr th:fragment="edit-row" th:id="'row-' + ${item.id}">
    <td>
        <input name="fullName" th:value="${item.fullName}">
    </td>
    <td>
        <input name="email" th:value="${item.email}">
    </td>
    <td>
        <button hx-put="@{'/registrations/' + ${item.id}}"
                hx-target="@{'#row-' + ${item.id}}"
                hx-swap="outerHTML"
                hx-include="closest tr">
            Save
        </button>
        <button hx-get="@{'/registrations/' + ${item.id} + '/view'}"
                hx-target="@{'#row-' + ${item.id}}"
                hx-swap="outerHTML">
            Cancel
        </button>
    </td>
</tr>
```

### 5.3 Delete with Confirmation

```html
<button x-data="confirmDialog"
        x-on:click="show('Delete Registration', 'Are you sure you want to delete this registration?', () => htmx.ajax('DELETE', '/registrations/[[${item.id}]]', {target: '#row-[[${item.id}]]', swap: 'outerHTML'}))">
    Delete
</button>

<!-- Or using htmx confirm -->
<button hx-delete="@{'/registrations/' + ${item.id}}"
        hx-target="@{'#row-' + ${item.id}}"
        hx-swap="outerHTML"
        hx-confirm="Are you sure you want to delete this registration?">
    Delete
</button>
```

---

## Phase 6: Real-Time Features Migration

### 6.1 Polling for Updates

**Before (JavaScript setInterval):**
```javascript
setInterval(() => {
    fetch('/api/alerts')
        .then(r => r.json())
        .then(data => updateAlerts(data));
}, 30000);
```

**After (htmx polling):**
```html
<div id="alerts-container"
     hx-get="/api/alerts"
     hx-trigger="every 30s"
     hx-swap="innerHTML">
    <!-- Alert content rendered server-side -->
</div>
```

### 6.2 Session Timer with Alpine.js

```html
<div x-data="sessionTimer('[[${session.startTime}]]')"
     x-init="start()"
     x-on:session-ended.window="stop()">
    <span x-text="formatted()">00:00:00</span>
</div>
```

### 6.3 Live Search

```html
<input type="search"
       name="query"
       placeholder="Search..."
       hx-get="/api/search"
       hx-trigger="keyup changed delay:300ms"
       hx-target="#search-results"
       hx-indicator="#search-spinner">

<span id="search-spinner" class="htmx-indicator">
    <i class="fas fa-spinner fa-spin"></i>
</span>

<div id="search-results"></div>
```

---

## Phase 7: Feedback Form Migration

### 7.1 Auto-Save with Status Indicators

```html
<form x-data="autoSave('/api/feedback/draft', 3000)"
      hx-post="/api/feedback/submit"
      hx-target="#submission-result">

    <!-- Save Status Indicator -->
    <div class="flex items-center gap-2">
        <template x-if="status === 'saving'">
            <span class="text-yellow-600">
                <i class="fas fa-spinner fa-spin"></i> Saving...
            </span>
        </template>
        <template x-if="status === 'saved'">
            <span class="text-green-600">
                <i class="fas fa-check"></i> Saved
            </span>
        </template>
        <template x-if="status === 'error'">
            <span class="text-red-600">
                <i class="fas fa-exclamation-triangle"></i> Save failed
            </span>
        </template>
    </div>

    <!-- Question with auto-save -->
    <div class="mb-6">
        <label>Your feedback</label>
        <textarea name="feedback"
                  x-on:input="triggerSave({feedback: $event.target.value})"
                  class="w-full border rounded p-2"></textarea>
    </div>

    <button type="submit">Submit Feedback</button>
</form>
```

### 7.2 Star Rating Component

```html
<div x-data="starRating(0)" class="flex gap-1">
    <input type="hidden" name="rating" x-bind:value="rating">

    <template x-for="star in [1, 2, 3, 4, 5]" :key="star">
        <button type="button"
                x-on:click="setRating(star)"
                x-on:mouseenter="setHover(star)"
                x-on:mouseleave="clearHover()"
                x-bind:class="{ 'text-yellow-400': isActive(star), 'text-gray-300': !isActive(star) }"
                class="text-2xl transition-colors">
            <i class="fas fa-star"></i>
        </button>
    </template>
</div>
```

### 7.3 Character Counter

```html
<div x-data="charCounter(500)">
    <textarea name="comments"
              maxlength="500"
              x-on:input="update($event.target.value)"
              class="w-full border rounded p-2"></textarea>
    <div class="text-sm text-right"
         x-bind:class="{ 'text-red-600': isOverLimit(), 'text-gray-500': !isOverLimit() }">
        <span x-text="count">0</span>/<span x-text="max">500</span>
    </div>
</div>
```

---

## Phase 8: Instructor Session Management Migration

### 8.1 Session State Management

```html
<div x-data="sessionState" x-init="init()">
    <!-- Check-in Button -->
    <button x-show="!isCheckedIn"
            x-on:click="showCheckInModal = true"
            class="bg-blue-500 text-white px-4 py-2 rounded">
        Check In
    </button>

    <!-- Session Controls (after check-in) -->
    <div x-show="isCheckedIn">
        <button x-show="!isStarted"
                hx-post="/api/session/start"
                hx-target="#session-status"
                x-on:htmx:after-request="isStarted = true"
                class="bg-green-500 text-white px-4 py-2 rounded">
            Start Session
        </button>

        <button x-show="isStarted"
                hx-post="/api/session/end"
                hx-target="#session-status"
                hx-confirm="Are you sure you want to end this session?"
                class="bg-red-500 text-white px-4 py-2 rounded">
            End Session
        </button>
    </div>

    <div id="session-status"></div>
</div>
```

**Add to Alpine components:**

```javascript
Alpine.data('sessionState', () => ({
    isCheckedIn: false,
    isStarted: false,
    isLate: false,
    showCheckInModal: false,
    checkInTime: null,

    init() {
        // Load initial state from server
        this.loadState();
    },

    async loadState() {
        const response = await fetch('/api/session/state');
        const data = await response.json();
        this.isCheckedIn = data.isCheckedIn;
        this.isStarted = data.isStarted;
        this.checkInTime = data.checkInTime;
    },

    checkIn(data) {
        this.isCheckedIn = true;
        this.checkInTime = new Date();
        this.showCheckInModal = false;
    }
}));
```

### 8.2 Equipment Issue Modal

```html
<!-- Trigger Button -->
<button x-on:click="$dispatch('open-equipment-modal')"
        class="bg-yellow-500 text-white px-4 py-2 rounded">
    <i class="fas fa-tools"></i> Report Issue
</button>

<!-- Modal -->
<div x-data="modal"
     x-on:open-equipment-modal.window="show()"
     x-show="open"
     class="fixed inset-0 z-50">

    <div class="fixed inset-0 bg-gray-600 bg-opacity-50" x-on:click="hide()"></div>

    <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <h3 class="text-lg font-medium mb-4">Report Equipment Issue</h3>

        <form hx-post="/api/equipment-issues"
              hx-target="#issue-result"
              x-on:htmx:after-request="hide()">

            <div class="mb-4">
                <label>Equipment Type</label>
                <select name="equipmentType" required class="w-full border rounded p-2">
                    <option value="">Select type...</option>
                    <option value="PROJECTOR">Projector</option>
                    <option value="SOUND_SYSTEM">Sound System</option>
                    <option value="MICROPHONE">Microphone</option>
                    <option value="COMPUTER">Computer</option>
                    <option value="LIGHTING">Lighting</option>
                    <option value="AIR_CONDITIONING">Air Conditioning</option>
                    <option value="WHITEBOARD">Whiteboard</option>
                    <option value="FURNITURE">Furniture</option>
                    <option value="OTHER">Other</option>
                </select>
            </div>

            <div class="mb-4">
                <label>Description</label>
                <textarea name="description" required class="w-full border rounded p-2"></textarea>
            </div>

            <div class="mb-4">
                <label class="flex items-center gap-2">
                    <input type="checkbox" name="isUrgent" value="true">
                    <span>Urgent Issue</span>
                </label>
            </div>

            <div class="flex justify-end gap-2">
                <button type="button" x-on:click="hide()"
                        class="px-4 py-2 border rounded">
                    Cancel
                </button>
                <button type="submit"
                        class="px-4 py-2 bg-blue-500 text-white rounded">
                    Submit
                </button>
            </div>
        </form>
    </div>
</div>

<div id="issue-result"></div>
```

---

## Phase 9: Controller Updates for htmx

### 9.1 Fragment Controller Pattern

```java
@Controller
@RequestMapping("/registrations")
public class RegistrationController {

    @GetMapping
    public String list(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpServletRequest request) {

        Page<StudentRegistration> registrations = registrationService.search(fullName, status, PageRequest.of(page, 20));
        model.addAttribute("items", registrations.getContent());
        model.addAttribute("page", registrations);

        // Return fragment for htmx requests
        if (isHtmxRequest(request)) {
            return "registrations/fragments :: table-rows";
        }

        return "registrations/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model, HttpServletRequest request) {
        StudentRegistration registration = registrationService.findById(id);
        model.addAttribute("registration", registration);

        if (isHtmxRequest(request)) {
            return "registrations/fragments :: detail-content";
        }

        return "registrations/detail";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id, HttpServletRequest request) {
        registrationService.delete(id);

        if (isHtmxRequest(request)) {
            return "fragments/empty :: empty"; // Returns empty element for swap
        }

        return "redirect:/registrations";
    }

    private boolean isHtmxRequest(HttpServletRequest request) {
        return "true".equals(request.getHeader("HX-Request"));
    }
}
```

### 9.2 htmx Response Headers

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/feedback/submit")
    public ResponseEntity<String> submitFeedback(
            @RequestBody FeedbackRequest request,
            HttpServletResponse response) {

        feedbackService.submit(request);

        // htmx redirect header
        response.setHeader("HX-Redirect", "/feedback/confirmation");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/session/start")
    public ResponseEntity<String> startSession(
            @RequestParam UUID sessionId,
            HttpServletResponse response) {

        sessionService.start(sessionId);

        // htmx trigger for client-side event
        response.setHeader("HX-Trigger", "session-started");

        return ResponseEntity.ok("<div class='text-green-600'>Session started</div>");
    }
}
```

---

## Phase 10: Migration Order

### Priority 1: Foundation (Week 1-2)
1. Update `main.html` layout with htmx and Alpine.js CSP
2. Create `htmx-config.js` for CSRF handling
3. Create `alpine-components.js` with all component registrations
4. Update `SecurityConfig.java` with CSP headers
5. Migrate navigation dropdowns to Alpine.js CSP

### Priority 2: High-Traffic Pages (Week 3-4)
1. `registrations/list.html` - Search, filter, pagination
2. `instructor/dashboard.html` - Main instructor view
3. `management/term-preparation-dashboard.html` - Management overview
4. `student/feedback-form.html` - Auto-save, star rating

### Priority 3: Complex Workflows (Week 5-6)
1. `instructor/session-management.html` - Full session workflow
2. `registration/form.html` - Multi-step registration
3. `monitoring/sessions.html` - Real-time monitoring
4. `academic/semester-closure-dashboard.html`

### Priority 4: Remaining Templates (Week 7-8)
1. All remaining instructor templates
2. All remaining management templates
3. All remaining academic templates
4. Error pages and confirmation pages

---

## Testing Strategy

### 1. Unit Tests for Alpine.js Components

Create test file `src/test/resources/alpine-component-tests.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <script src="https://cdn.jsdelivr.net/npm/@alpinejs/csp@3.15.2/dist/cdn.min.js" defer></script>
    <script src="/js/alpine-components.js"></script>
</head>
<body>
    <!-- Test harness for Alpine components -->
    <div x-data="dropdown" id="test-dropdown">
        <button x-on:click="toggle" data-testid="toggle">Toggle</button>
        <div x-show="open" data-testid="content">Content</div>
    </div>
</body>
</html>
```

### 2. Playwright Tests for htmx Interactions

```java
@Test
void searchShouldUpdateTableWithoutPageReload() {
    page.navigate("/registrations");

    // Type in search box
    page.fill("#search-input", "Ahmad");

    // Wait for htmx to complete
    page.waitForSelector("[hx-indicator]:not(.htmx-request)");

    // Verify table was updated
    assertThat(page.locator("table tbody tr")).hasCount(greaterThan(0));

    // Verify URL was updated (hx-push-url)
    assertThat(page.url()).contains("fullName=Ahmad");
}

@Test
void modalShouldOpenAndCloseCorrectly() {
    page.navigate("/registrations");

    // Click view button
    page.click("button:has-text('View')");

    // Wait for modal to appear
    page.waitForSelector("[x-show='open']:visible");

    // Verify modal content loaded via htmx
    assertThat(page.locator("#detail-modal-content")).not().isEmpty();

    // Close modal
    page.click("button:has-text('Close')");

    // Verify modal is hidden
    page.waitForSelector("[x-show='open']:hidden");
}
```

---

## Rollback Strategy

If issues arise during migration:

1. **Per-template rollback**: Keep original JavaScript in `<script>` tags, just commented out
2. **Feature flags**: Use Thymeleaf conditions to switch between old and new implementations
3. **Progressive migration**: Each template can work independently

```html
<!-- Feature flag for htmx migration -->
<th:block th:if="${@featureFlags.isHtmxEnabled()}">
    <!-- htmx implementation -->
</th:block>
<th:block th:unless="${@featureFlags.isHtmxEnabled()}">
    <!-- Original JavaScript implementation -->
</th:block>
```

---

## Security Checklist

- [ ] CSP headers configured in SecurityConfig
- [ ] CSRF tokens included in all htmx requests
- [ ] No inline JavaScript (Alpine.js CSP build)
- [ ] All external scripts from trusted CDNs
- [ ] XSS protection via Thymeleaf escaping
- [ ] Input validation on all htmx endpoints
- [ ] Rate limiting on API endpoints
- [ ] DAST scan passes with no critical findings

---

## Monitoring & Debugging

### htmx Debugging

```javascript
// Enable htmx debug logging
htmx.logAll();

// Or for specific events
htmx.logger = function(elt, event, data) {
    if (event === 'htmx:afterSwap') {
        console.log('Swap completed:', elt, data);
    }
};
```

### Alpine.js Debugging

```javascript
// Add to alpine-components.js
Alpine.directive('log', (el, { expression }, { evaluate }) => {
    console.log('Alpine state:', evaluate(expression));
});

// Usage: <div x-log="$data"></div>
```

---

## Performance Considerations

1. **Lazy loading**: Use `hx-trigger="revealed"` for below-fold content
2. **Request deduplication**: htmx automatically handles duplicate requests
3. **Response caching**: Add `hx-swap="innerHTML show:top settle:0ms"` for instant swaps
4. **Progress indicators**: Always include `hx-indicator` for user feedback
5. **Optimistic updates**: Use `hx-swap="outerHTML swap:0s"` for immediate visual feedback
