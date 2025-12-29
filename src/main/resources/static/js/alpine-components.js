/**
 * Alpine.js CSP-Compliant Components Registry
 *
 * All x-data components must be registered here for CSP compliance.
 * The CSP build does not allow inline expressions like x-data="{ open: false }".
 * Instead, use x-data="dropdown" and register components via Alpine.data().
 */

document.addEventListener('alpine:init', () => {

    // ===================
    // Navigation Components
    // ===================

    /**
     * Dropdown Component
     * Usage: <div x-data="dropdown">
     *          <button x-on:click="toggle">Menu</button>
     *          <div x-show="open" x-on:click.away="close">Content</div>
     *        </div>
     */
    Alpine.data('dropdown', () => ({
        open: false,
        toggle() {
            this.open = !this.open;
        },
        close() {
            this.open = false;
        }
    }));

    /**
     * Mobile Menu Component
     * Usage: <nav x-data="mobileMenu">
     *          <button x-on:click="toggle">Menu</button>
     *          <div x-show="open">Mobile Nav</div>
     *        </nav>
     */
    Alpine.data('mobileMenu', () => ({
        open: false,
        toggle() {
            this.open = !this.open;
        },
        close() {
            this.open = false;
        }
    }));

    // ===================
    // Modal Components
    // ===================

    /**
     * Modal Component
     * Usage: <div x-data="modal" x-on:open-modal.window="show()">
     *          <div x-show="open">Modal Content</div>
     *        </div>
     */
    Alpine.data('modal', () => ({
        open: false,
        show() {
            this.open = true;
            document.body.style.overflow = 'hidden';
        },
        hide() {
            this.open = false;
            document.body.style.overflow = 'auto';
        },
        toggle() {
            if (this.open) {
                this.hide();
            } else {
                this.show();
            }
        }
    }));

    /**
     * Confirmation Dialog Component
     * Usage: <div x-data="confirmDialog">
     *          <button x-on:click="show('Delete?', 'Are you sure?', deleteAction)">Delete</button>
     *          <div x-show="open">
     *            <h3 x-text="title"></h3>
     *            <p x-text="message"></p>
     *            <button x-on:click="confirm()">Yes</button>
     *            <button x-on:click="cancel()">No</button>
     *          </div>
     *        </div>
     */
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
            document.body.style.overflow = 'hidden';
        },
        confirm() {
            if (this.confirmAction) {
                this.confirmAction();
            }
            this.open = false;
            document.body.style.overflow = 'auto';
        },
        cancel() {
            this.open = false;
            this.confirmAction = null;
            document.body.style.overflow = 'auto';
        }
    }));

    // ===================
    // Form Components
    // ===================

    /**
     * Form Validation Component
     * Usage: <form x-data="formValidation">
     *          <input x-bind:class="{ 'border-red-500': hasError('email') }">
     *          <span x-text="getError('email')"></span>
     *        </form>
     */
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
        },
        setErrors(errorMap) {
            this.errors = errorMap;
        }
    }));

    /**
     * Star Rating Component
     * Usage: <div x-data="starRating">
     *          <input type="hidden" name="rating" x-bind:value="rating">
     *          <button x-on:click="setRating(1)" x-bind:class="{ 'text-yellow-400': isActive(1) }">★</button>
     *          ...
     *        </div>
     */
    Alpine.data('starRating', () => ({
        rating: 0,
        hoverRating: 0,
        init() {
            // Check for initial value from hidden input
            const input = this.$el.querySelector('input[type="hidden"]');
            if (input && input.value) {
                this.rating = parseInt(input.value) || 0;
            }
        },
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

    /**
     * Character Counter Component
     * Usage: <div x-data="charCounter">
     *          <textarea x-on:input="update($event.target.value)"></textarea>
     *          <span x-text="count"></span>/<span x-text="max"></span>
     *        </div>
     */
    Alpine.data('charCounter', () => ({
        count: 0,
        max: 500,
        init() {
            // Check for maxlength attribute
            const textarea = this.$el.querySelector('textarea');
            if (textarea && textarea.maxLength > 0) {
                this.max = textarea.maxLength;
                this.count = textarea.value.length;
            }
        },
        update(value) {
            this.count = value.length;
        },
        remaining() {
            return this.max - this.count;
        },
        isOverLimit() {
            return this.count > this.max;
        },
        isNearLimit() {
            return this.count > this.max * 0.9;
        }
    }));

    /**
     * Tab Navigation Component
     * Usage: <div x-data="tabs">
     *          <button x-on:click="setTab(0)" x-bind:class="{ 'active': isActive(0) }">Tab 1</button>
     *          <div x-show="isActive(0)">Content 1</div>
     *        </div>
     */
    Alpine.data('tabs', () => ({
        activeTab: 0,
        init() {
            // Check for data-initial-tab attribute
            const initialTab = this.$el.dataset.initialTab;
            if (initialTab !== undefined) {
                this.activeTab = parseInt(initialTab) || 0;
            }
        },
        setTab(index) {
            this.activeTab = index;
        },
        isActive(index) {
            return this.activeTab === index;
        },
        nextTab() {
            this.activeTab++;
        },
        prevTab() {
            if (this.activeTab > 0) {
                this.activeTab--;
            }
        }
    }));

    // ===================
    // Auto-Save Component
    // ===================

    /**
     * Auto-Save Component
     * Usage: <form x-data="autoSave" data-save-url="/api/save">
     *          <textarea x-on:input="triggerSave({content: $event.target.value})"></textarea>
     *          <span x-show="status === 'saving'">Saving...</span>
     *          <span x-show="status === 'saved'">Saved</span>
     *        </form>
     */
    Alpine.data('autoSave', () => ({
        status: 'idle', // idle, saving, saved, error
        timer: null,
        saveUrl: '',
        debounceMs: 3000,
        init() {
            this.saveUrl = this.$el.dataset.saveUrl || '';
            const debounce = this.$el.dataset.debounceMs;
            if (debounce) {
                this.debounceMs = parseInt(debounce) || 3000;
            }
        },
        triggerSave(data) {
            clearTimeout(this.timer);
            this.status = 'saving';
            this.timer = setTimeout(() => {
                this.performSave(data);
            }, this.debounceMs);
        },
        async performSave(data) {
            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const response = await fetch(this.saveUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(data)
                });
                this.status = response.ok ? 'saved' : 'error';
                if (this.status === 'saved') {
                    setTimeout(() => {
                        if (this.status === 'saved') {
                            this.status = 'idle';
                        }
                    }, 2000);
                }
            } catch (e) {
                this.status = 'error';
                console.error('Auto-save error:', e);
            }
        },
        resetStatus() {
            this.status = 'idle';
        }
    }));

    // ===================
    // Timer Components
    // ===================

    /**
     * Session Timer Component
     * Usage: <div x-data="sessionTimer" data-start-time="2024-01-01T10:00:00">
     *          <span x-text="formatted()">00:00:00</span>
     *        </div>
     */
    Alpine.data('sessionTimer', () => ({
        elapsed: 0,
        interval: null,
        startTime: null,
        init() {
            const startTimeStr = this.$el.dataset.startTime;
            if (startTimeStr) {
                this.startTime = new Date(startTimeStr).getTime();
                this.start();
            }
        },
        start() {
            if (!this.startTime) {
                this.startTime = Date.now();
            }
            this.interval = setInterval(() => {
                this.elapsed = Math.floor((Date.now() - this.startTime) / 1000);
            }, 1000);
        },
        stop() {
            clearInterval(this.interval);
        },
        reset() {
            this.stop();
            this.elapsed = 0;
            this.startTime = null;
        },
        formatted() {
            const hours = Math.floor(this.elapsed / 3600);
            const minutes = Math.floor((this.elapsed % 3600) / 60);
            const seconds = this.elapsed % 60;
            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        },
        destroy() {
            this.stop();
        }
    }));

    /**
     * Countdown Timer Component
     * Usage: <div x-data="countdown" data-end-time="2024-01-01T12:00:00">
     *          <span x-text="formatted()">00:00:00</span>
     *        </div>
     */
    Alpine.data('countdown', () => ({
        remaining: 0,
        interval: null,
        endTime: null,
        expired: false,
        init() {
            const endTimeStr = this.$el.dataset.endTime;
            if (endTimeStr) {
                this.endTime = new Date(endTimeStr).getTime();
                this.start();
            }
        },
        start() {
            this.updateRemaining();
            this.interval = setInterval(() => {
                this.updateRemaining();
            }, 1000);
        },
        updateRemaining() {
            const diff = this.endTime - Date.now();
            if (diff <= 0) {
                this.remaining = 0;
                this.expired = true;
                this.stop();
                this.$dispatch('countdown-expired');
            } else {
                this.remaining = Math.floor(diff / 1000);
            }
        },
        stop() {
            clearInterval(this.interval);
        },
        formatted() {
            const hours = Math.floor(this.remaining / 3600);
            const minutes = Math.floor((this.remaining % 3600) / 60);
            const seconds = this.remaining % 60;
            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        },
        destroy() {
            this.stop();
        }
    }));

    // ===================
    // Polling Component
    // ===================

    /**
     * Polling Component (for real-time updates without htmx)
     * Usage: <div x-data="polling" data-poll-url="/api/status" data-poll-interval="30000">
     *          <span x-text="data?.status"></span>
     *        </div>
     */
    Alpine.data('polling', () => ({
        data: null,
        error: null,
        loading: false,
        interval: null,
        pollUrl: '',
        pollIntervalMs: 30000,
        init() {
            this.pollUrl = this.$el.dataset.pollUrl || '';
            const interval = this.$el.dataset.pollInterval;
            if (interval) {
                this.pollIntervalMs = parseInt(interval) || 30000;
            }
            if (this.pollUrl) {
                this.start();
            }
        },
        async fetch() {
            this.loading = true;
            try {
                const response = await fetch(this.pollUrl);
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                this.data = await response.json();
                this.error = null;
            } catch (e) {
                this.error = e.message;
                console.error('Polling error:', e);
            } finally {
                this.loading = false;
            }
        },
        start() {
            this.fetch();
            this.interval = setInterval(() => this.fetch(), this.pollIntervalMs);
        },
        stop() {
            clearInterval(this.interval);
        },
        restart() {
            this.stop();
            this.start();
        },
        destroy() {
            this.stop();
        }
    }));

    // ===================
    // Session Management Components (Instructor)
    // ===================

    /**
     * Session Management Component
     * Main component for instructor session management page
     * Usage: <div x-data="sessionManagement" data-is-late="true">
     */
    Alpine.data('sessionManagement', () => ({
        isLate: false,
        isCheckedIn: false,
        isStarted: false,
        checkInModalOpen: false,
        emergencyMenuOpen: false,
        equipmentFormOpen: false,
        emergencyTerminationOpen: false,

        // Form data
        arrivalTime: '',
        location: '',
        lateReason: '',
        validationError: '',
        submitting: false,

        // Equipment form
        equipmentDescription: '',
        equipmentType: '',
        isUrgent: false,

        // Emergency termination
        emergencyReason: '',
        emergencyType: 'FIRE_EVACUATION',

        init() {
            this.isLate = this.$el.dataset.isLate === 'true';
            // Set current time as default arrival time
            const now = new Date();
            this.arrivalTime = now.toTimeString().slice(0, 5);
        },

        // Check-in Modal
        openCheckInModal() {
            this.checkInModalOpen = true;
            this.validationError = '';
            document.body.style.overflow = 'hidden';
        },

        closeCheckInModal() {
            this.checkInModalOpen = false;
            this.resetCheckInForm();
            document.body.style.overflow = 'auto';
        },

        resetCheckInForm() {
            this.location = '';
            this.lateReason = '';
            this.validationError = '';
            const now = new Date();
            this.arrivalTime = now.toTimeString().slice(0, 5);
        },

        async performCheckIn() {
            // Validate late reason if late
            if (this.isLate && (!this.lateReason || this.lateReason.trim() === '')) {
                this.validationError = 'Alasan keterlambatan harus diisi';
                return;
            }

            this.submitting = true;
            this.validationError = '';

            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

                const response = await fetch('/instructor/check-in', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        location: this.location,
                        arrivalTime: this.arrivalTime,
                        lateReason: this.lateReason
                    })
                });

                const data = await response.json();

                if (data.success) {
                    this.isCheckedIn = true;
                    this.closeCheckInModal();
                    this.$dispatch('show-alert', { message: 'Check-in berhasil!', type: 'success' });
                } else {
                    this.validationError = data.message || 'Check-in gagal';
                }
            } catch (error) {
                console.error('Check-in error:', error);
                this.validationError = 'Terjadi kesalahan saat check-in';
            } finally {
                this.submitting = false;
            }
        },

        // Emergency Options
        openEmergencyMenu() {
            this.emergencyMenuOpen = true;
            document.body.style.overflow = 'hidden';
        },

        closeEmergencyMenu() {
            this.emergencyMenuOpen = false;
            document.body.style.overflow = 'auto';
        },

        selectEquipmentIssue() {
            this.closeEmergencyMenu();
            this.equipmentFormOpen = true;
            document.body.style.overflow = 'hidden';
        },

        closeEquipmentForm() {
            this.equipmentFormOpen = false;
            this.resetEquipmentForm();
            document.body.style.overflow = 'auto';
        },

        resetEquipmentForm() {
            this.equipmentDescription = '';
            this.equipmentType = '';
            this.isUrgent = false;
        },

        async submitEquipmentIssue() {
            this.submitting = true;

            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

                const response = await fetch('/instructor/equipment-issue', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        description: this.equipmentDescription,
                        equipmentType: this.equipmentType,
                        isUrgent: this.isUrgent
                    })
                });

                if (response.ok) {
                    this.closeEquipmentForm();
                    this.$dispatch('show-alert', { message: 'Laporan masalah peralatan berhasil dikirim', type: 'success' });
                } else {
                    this.$dispatch('show-alert', { message: 'Gagal mengirim laporan', type: 'error' });
                }
            } catch (error) {
                console.error('Equipment issue error:', error);
                this.$dispatch('show-alert', { message: 'Terjadi kesalahan', type: 'error' });
            } finally {
                this.submitting = false;
            }
        },

        selectEmergencyTermination() {
            this.closeEmergencyMenu();
            this.emergencyTerminationOpen = true;
            document.body.style.overflow = 'hidden';
        },

        closeEmergencyTermination() {
            this.emergencyTerminationOpen = false;
            this.emergencyReason = '';
            this.emergencyType = 'FIRE_EVACUATION';
            document.body.style.overflow = 'auto';
        },

        async confirmEmergencyTermination() {
            if (!this.emergencyReason || this.emergencyReason.trim() === '') {
                this.$dispatch('show-alert', { message: 'Alasan harus diisi', type: 'error' });
                return;
            }

            this.submitting = true;

            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

                const response = await fetch('/instructor/emergency-termination', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        reason: this.emergencyReason,
                        type: this.emergencyType
                    })
                });

                if (response.ok) {
                    this.closeEmergencyTermination();
                    this.$dispatch('show-alert', { message: 'Sesi dihentikan darurat', type: 'warning' });
                } else {
                    this.$dispatch('show-alert', { message: 'Gagal menghentikan sesi', type: 'error' });
                }
            } catch (error) {
                console.error('Emergency termination error:', error);
                this.$dispatch('show-alert', { message: 'Terjadi kesalahan', type: 'error' });
            } finally {
                this.submitting = false;
            }
        },

        closeAllModals() {
            this.checkInModalOpen = false;
            this.emergencyMenuOpen = false;
            this.equipmentFormOpen = false;
            this.emergencyTerminationOpen = false;
            document.body.style.overflow = 'auto';
        }
    }));

    /**
     * Session State Component for Instructor Session Management
     * Usage: <div x-data="sessionState" data-session-id="uuid">
     *          <button x-show="!isCheckedIn" x-on:click="showCheckInModal = true">Check In</button>
     *        </div>
     */
    Alpine.data('sessionState', () => ({
        sessionId: '',
        isCheckedIn: false,
        isStarted: false,
        isEnded: false,
        isLate: false,
        checkInTime: null,
        startTime: null,
        showCheckInModal: false,
        showEndSessionModal: false,
        showEmergencyModal: false,
        showEquipmentModal: false,
        showGuestModal: false,
        init() {
            this.sessionId = this.$el.dataset.sessionId || '';
            this.loadState();
        },
        async loadState() {
            if (!this.sessionId) return;
            try {
                const response = await fetch(`/api/session/${this.sessionId}/state`);
                if (response.ok) {
                    const data = await response.json();
                    this.isCheckedIn = data.isCheckedIn || false;
                    this.isStarted = data.isStarted || false;
                    this.isEnded = data.isEnded || false;
                    this.isLate = data.isLate || false;
                    this.checkInTime = data.checkInTime;
                    this.startTime = data.startTime;
                }
            } catch (e) {
                console.error('Failed to load session state:', e);
            }
        },
        onCheckIn() {
            this.isCheckedIn = true;
            this.checkInTime = new Date().toISOString();
            this.showCheckInModal = false;
        },
        onSessionStart() {
            this.isStarted = true;
            this.startTime = new Date().toISOString();
        },
        onSessionEnd() {
            this.isEnded = true;
            this.showEndSessionModal = false;
        },
        closeAllModals() {
            this.showCheckInModal = false;
            this.showEndSessionModal = false;
            this.showEmergencyModal = false;
            this.showEquipmentModal = false;
            this.showGuestModal = false;
            document.body.style.overflow = 'auto';
        }
    }));

    // ===================
    // Utility Components
    // ===================

    /**
     * Toggle Component (simple boolean toggle)
     * Usage: <div x-data="toggle">
     *          <button x-on:click="toggle()">Toggle</button>
     *          <div x-show="active">Content</div>
     *        </div>
     */
    Alpine.data('toggle', () => ({
        active: false,
        init() {
            const initial = this.$el.dataset.initialState;
            if (initial === 'true') {
                this.active = true;
            }
        },
        toggle() {
            this.active = !this.active;
        },
        show() {
            this.active = true;
        },
        hide() {
            this.active = false;
        }
    }));

    /**
     * Accordion Component
     * Usage: <div x-data="accordion">
     *          <button x-on:click="toggleItem(0)">Section 1</button>
     *          <div x-show="isOpen(0)">Content 1</div>
     *        </div>
     */
    Alpine.data('accordion', () => ({
        openItems: [],
        allowMultiple: false,
        init() {
            this.allowMultiple = this.$el.dataset.allowMultiple === 'true';
        },
        toggleItem(index) {
            if (this.isOpen(index)) {
                this.openItems = this.openItems.filter(i => i !== index);
            } else {
                if (this.allowMultiple) {
                    this.openItems.push(index);
                } else {
                    this.openItems = [index];
                }
            }
        },
        isOpen(index) {
            return this.openItems.includes(index);
        },
        closeAll() {
            this.openItems = [];
        }
    }));

    /**
     * Clipboard Component
     * Usage: <div x-data="clipboard">
     *          <input x-ref="source" value="text to copy">
     *          <button x-on:click="copy()">Copy</button>
     *          <span x-show="copied">Copied!</span>
     *        </div>
     */
    Alpine.data('clipboard', () => ({
        copied: false,
        copyText: '',
        async copy() {
            const source = this.$refs.source;
            const text = source ? (source.value || source.textContent) : this.copyText;
            try {
                await navigator.clipboard.writeText(text);
                this.copied = true;
                setTimeout(() => {
                    this.copied = false;
                }, 2000);
            } catch (e) {
                console.error('Failed to copy:', e);
            }
        }
    }));

    /**
     * Alert/Toast Component
     * Usage: <div x-data="alert">
     *          <div x-show="visible" x-text="message"></div>
     *        </div>
     *        Then dispatch: $dispatch('show-alert', { message: 'Hello', type: 'success' })
     */
    Alpine.data('alert', () => ({
        visible: false,
        message: '',
        type: 'info', // info, success, warning, error
        timeout: null,
        init() {
            this.$watch('visible', (value) => {
                if (value) {
                    clearTimeout(this.timeout);
                    this.timeout = setTimeout(() => {
                        this.visible = false;
                    }, 5000);
                }
            });
        },
        show(message, type = 'info') {
            this.message = message;
            this.type = type;
            this.visible = true;
        },
        hide() {
            this.visible = false;
        }
    }));

});
