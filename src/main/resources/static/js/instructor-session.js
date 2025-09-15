/**
 * Instructor Session Management JavaScript
 * Handles session check-in, late validation, emergency management, and attendance tracking
 */

// Session state management
let sessionState = {
    isCheckedIn: false,
    isSessionStarted: false,
    isLate: false,
    sessionStartTime: null,
    checkInTime: null
};

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    initializeSessionManagement();
    checkSessionStatus();
    setupEventListeners();
});

function initializeSessionManagement() {
    console.log('Initializing session management...');

    // Use server-side data if available, otherwise fall back to client-side logic
    if (window.serverSessionData) {
        console.log('Using server-side session data:', window.serverSessionData);
        sessionState.isLate = window.serverSessionData.isSessionLate || false;

        // Initialize check-in status from server data
        if (window.serverSessionData.sessionData && window.serverSessionData.sessionData.isCheckedIn) {
            sessionState.isCheckedIn = window.serverSessionData.sessionData.isCheckedIn;
        }
    } else {
        console.log('No server data found, using client-side lateness detection');
        // Check if session is late based on current time (fallback)
        checkLateness();
    }

    console.log('Session state initialized:', sessionState);

    // Initialize modal close functions
    window.closeModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            modal.classList.add('hidden');
            console.log('Closing modal:', modalId);
        }
    };

    // Initialize modal show functions
    window.showModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
            modal.style.display = 'block';
            console.log('Showing modal:', modalId, 'Modal element:', modal);
        } else {
            console.error('Modal not found:', modalId);
        }
    };
}

function checkLateness() {
    // Simulate session being late for testing purposes
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();

    // For testing: assume session starts at 8:00 AM and late threshold is 15 minutes
    // In real implementation, this would come from server data
    const sessionStartHour = 8;
    const sessionStartMinute = 0;
    const lateThresholdMinutes = 15;

    const sessionStart = new Date();
    sessionStart.setHours(sessionStartHour, sessionStartMinute, 0, 0);

    const lateThreshold = new Date(sessionStart.getTime() + lateThresholdMinutes * 60000);

    if (now > lateThreshold) {
        sessionState.isLate = true;
        showLateSessionWarning();
    }
}

function showLateSessionWarning() {
    const lateWarning = document.getElementById('late-session-warning');
    const lateBadge = document.getElementById('late-badge');

    if (lateWarning) {
        lateWarning.style.display = 'block';
    }
    if (lateBadge) {
        lateBadge.style.display = 'inline-block';
    }

    // Add overdue color coding to session card
    const sessionCard = document.getElementById('today-session');
    if (sessionCard) {
        sessionCard.classList.add('border-red-400', 'bg-red-50');
    }
}

function setupEventListeners() {
    // Check-in button
    const checkInBtn = document.getElementById('btn-check-in');
    if (checkInBtn) {
        checkInBtn.addEventListener('click', handleCheckInClick);
    }

    // Confirm check-in button
    const confirmCheckInBtn = document.getElementById('btn-confirm-check-in');
    if (confirmCheckInBtn) {
        confirmCheckInBtn.addEventListener('click', handleConfirmCheckIn);
    }

    // Start session button
    const startSessionBtn = document.getElementById('btn-start-session');
    if (startSessionBtn) {
        startSessionBtn.addEventListener('click', handleStartSession);
    }

    // Emergency options button
    const emergencyBtn = document.getElementById('btn-emergency-options');
    if (emergencyBtn) {
        emergencyBtn.addEventListener('click', () => showModal('emergency-options-menu'));
    }

    // Emergency option handlers
    const equipmentIssueBtn = document.getElementById('equipment-issue-option');
    if (equipmentIssueBtn) {
        equipmentIssueBtn.addEventListener('click', () => {
            closeModal('emergency-options-menu');
            showModal('equipment-issue-modal');
        });
    }

    const emergencyTerminationBtn = document.getElementById('emergency-termination-option');
    if (emergencyTerminationBtn) {
        emergencyTerminationBtn.addEventListener('click', () => {
            closeModal('emergency-options-menu');
            showModal('emergency-termination-modal');
        });
    }

    // Submit handlers
    const submitEquipmentIssueBtn = document.getElementById('btn-submit-equipment-issue');
    if (submitEquipmentIssueBtn) {
        submitEquipmentIssueBtn.addEventListener('click', handleSubmitEquipmentIssue);
    }

    const confirmEmergencyTerminationBtn = document.getElementById('btn-confirm-emergency-termination');
    if (confirmEmergencyTerminationBtn) {
        confirmEmergencyTerminationBtn.addEventListener('click', handleConfirmEmergencyTermination);
    }
}

function handleCheckInClick() {
    console.log('Check-in button clicked, session state:', sessionState);
    showModal('modal-check-in');

    // Set current time as arrival time
    const arrivalTimeInput = document.getElementById('arrival-time');
    if (arrivalTimeInput) {
        const now = new Date();
        const timeString = now.toTimeString().slice(0, 5);
        arrivalTimeInput.value = timeString;
    }

    // Show late check-in warning if session is late
    if (sessionState.isLate) {
        showLateCheckInValidation();
    }
}

function showLateCheckInValidation() {
    const lateWarning = document.getElementById('late-checkin-modal-warning');
    const reasonField = document.getElementById('late-checkin-reason-field');

    if (lateWarning) {
        lateWarning.style.display = 'block';
    }
    if (reasonField) {
        reasonField.style.display = 'block';
    }
}

function handleConfirmCheckIn() {
    const arrivalTime = document.getElementById('arrival-time').value;
    const location = document.getElementById('check-in-location').value;

    // Validate required fields
    if (!arrivalTime || !location) {
        showValidationError('Waktu tiba dan lokasi harus diisi');
        return;
    }

    // Validate late check-in reason if required
    if (sessionState.isLate) {
        const lateReason = document.getElementById('late-checkin-reason').value;
        if (!lateReason.trim()) {
            showValidationError('Alasan keterlambatan harus diisi untuk check-in terlambat');
            highlightReasonField();
            return;
        }
    }

    // Process check-in
    processCheckIn(arrivalTime, location);
}

function showValidationError(message) {
    const errorDiv = document.getElementById('validation-error');
    const errorMessage = document.getElementById('validation-error-message');

    if (errorDiv && errorMessage) {
        errorMessage.textContent = message;
        errorDiv.style.display = 'block';

        // Hide error after 5 seconds
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    }
}

function highlightReasonField() {
    const reasonField = document.getElementById('late-checkin-reason');
    if (reasonField) {
        reasonField.classList.add('border-red-500', 'ring-red-500');
        reasonField.focus();

        // Remove highlight after user starts typing
        reasonField.addEventListener('input', function() {
            this.classList.remove('border-red-500', 'ring-red-500');
        }, { once: true });
    }
}

function processCheckIn(arrivalTime, location) {
    sessionState.isCheckedIn = true;
    sessionState.checkInTime = new Date();

    // Close modal
    closeModal('modal-check-in');

    // Show success message
    if (sessionState.isLate) {
        showMessage('late-checkin-success');
    } else {
        showMessage('check-in-success');
    }

    // Enable start session button
    const startSessionBtn = document.getElementById('btn-start-session');
    if (startSessionBtn) {
        startSessionBtn.style.display = 'inline-block';
        startSessionBtn.disabled = false;
    }

    // Update session timer
    startSessionTimer();
}

function handleStartSession() {
    if (!sessionState.isCheckedIn) {
        alert('Anda harus check-in terlebih dahulu');
        return;
    }

    sessionState.isSessionStarted = true;
    sessionState.sessionStartTime = new Date();

    // Show attendance interface
    const attendanceInterface = document.getElementById('attendance-interface');
    if (attendanceInterface) {
        attendanceInterface.style.display = 'block';
    }

    // Hide start session button
    const startSessionBtn = document.getElementById('btn-start-session');
    if (startSessionBtn) {
        startSessionBtn.style.display = 'none';
    }

    // Initialize attendance tracking
    initializeAttendanceTracking();
}

function initializeAttendanceTracking() {
    // This would normally load student data from server
    // For testing purposes, create sample student list
    updateAttendanceCounter(0, 0);
}

function updateAttendanceCounter(present, total) {
    const counter = document.getElementById('attendance-counter');
    if (counter) {
        counter.textContent = `${present}/${total} Hadir`;
    }
}

function handleSubmitEquipmentIssue() {
    const equipmentType = document.getElementById('equipment-type').value;
    const description = document.getElementById('equipment-issue-description').value;
    const isUrgent = document.getElementById('mark-urgent').checked;

    if (!equipmentType || !description.trim()) {
        alert('Jenis peralatan dan deskripsi masalah harus diisi');
        return;
    }

    // Process equipment issue report
    console.log('Equipment issue reported:', { equipmentType, description, isUrgent });

    closeModal('equipment-issue-modal');
    showMessage('equipment-issue-reported');

    // Clear form
    document.getElementById('equipment-issue-form').reset();
}

function handleConfirmEmergencyTermination() {
    const emergencyType = document.getElementById('emergency-type').value;
    const emergencyReason = document.getElementById('emergency-reason').value;

    if (!emergencyType || !emergencyReason.trim()) {
        alert('Jenis darurat dan alasan darurat harus diisi');
        return;
    }

    // Process emergency termination
    console.log('Emergency termination:', { emergencyType, emergencyReason });

    // Terminate session immediately
    sessionState.isSessionStarted = false;

    closeModal('emergency-termination-modal');
    showMessage('emergency-termination-confirmed');

    // Disable all session controls
    disableAllSessionControls();
}

function disableAllSessionControls() {
    const controls = [
        'btn-check-in',
        'btn-start-session',
        'btn-end-session',
        'btn-emergency-options'
    ];

    controls.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.disabled = true;
            element.classList.add('opacity-50', 'cursor-not-allowed');
        }
    });
}

function showMessage(messageId) {
    const message = document.getElementById(messageId);
    if (message) {
        message.style.display = 'block';

        // Auto-hide after 5 seconds
        setTimeout(() => {
            message.style.display = 'none';
        }, 5000);
    }
}

function startSessionTimer() {
    const timerElement = document.getElementById('session-timer');
    if (!timerElement) return;

    timerElement.style.display = 'inline-block';

    const startTime = sessionState.checkInTime || new Date();

    function updateTimer() {
        const now = new Date();
        const elapsed = now - startTime;

        const hours = Math.floor(elapsed / 3600000);
        const minutes = Math.floor((elapsed % 3600000) / 60000);
        const seconds = Math.floor((elapsed % 60000) / 1000);

        timerElement.textContent = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }

    updateTimer();
    setInterval(updateTimer, 1000);
}

function checkSessionStatus() {
    // This function would typically check server for session status
    // For testing, we'll set up some defaults based on time

    const now = new Date();
    const hour = now.getHours();

    // If it's during typical class hours (8 AM - 5 PM), show today's session
    if (hour >= 8 && hour < 17) {
        const todaySession = document.getElementById('today-session');
        if (todaySession) {
            todaySession.style.display = 'block';
        }
    }
}

// Export functions for testing
window.sessionState = sessionState;
window.showLateSessionWarning = showLateSessionWarning;
window.handleCheckInClick = handleCheckInClick;
window.showLateCheckInValidation = showLateCheckInValidation;