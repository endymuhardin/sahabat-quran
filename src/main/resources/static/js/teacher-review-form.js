/**
 * Teacher Review Form JavaScript
 * Handles form validation and draft saving for teacher review page
 */

document.addEventListener('DOMContentLoaded', function() {
    // Dismiss alert functionality
    document.querySelectorAll('.dismiss-alert').forEach(function(btn) {
        btn.addEventListener('click', function() {
            this.parentElement.style.display = 'none';
        });
    });

    var form = document.getElementById('teacher-review-form');
    var reviewStatus = document.getElementById('reviewStatus');
    var teacherRemarks = document.getElementById('teacherRemarks');
    var submitBtn = document.getElementById('submit-review-btn');
    var saveDraftBtn = document.getElementById('save-draft-btn');

    if (!form) return;

    // Form validation
    form.addEventListener('submit', function(e) {
        var isValid = true;

        // Reset validation states
        [reviewStatus, teacherRemarks].forEach(function(field) {
            if (field) {
                field.classList.remove('is-invalid', 'border-red-500', 'bg-red-50');
                field.classList.add('border-gray-300');
            }
        });

        var statusErrorClient = document.getElementById('reviewStatus-error-client');
        var remarksErrorClient = document.getElementById('teacherRemarks-error-client');

        if (statusErrorClient) statusErrorClient.classList.add('hidden');
        if (remarksErrorClient) remarksErrorClient.classList.add('hidden');

        // Clear any existing recommended level validation
        var recommendedLevel = document.getElementById('recommendedLevelId');
        if (recommendedLevel) {
            recommendedLevel.classList.remove('is-invalid', 'border-red-500');
            var existingRecommendedError = recommendedLevel.parentElement.querySelector('.invalid-feedback, .recommended-level-error');
            if (existingRecommendedError) {
                existingRecommendedError.remove();
            }
        }

        // Validate review status
        if (reviewStatus && !reviewStatus.value) {
            reviewStatus.classList.add('is-invalid', 'border-red-500', 'bg-red-50');
            reviewStatus.classList.remove('border-gray-300');
            if (statusErrorClient) statusErrorClient.classList.remove('hidden');
            isValid = false;
        }

        // Validate teacher remarks (minimum 10 characters)
        if (teacherRemarks && (!teacherRemarks.value || teacherRemarks.value.trim().length < 10)) {
            teacherRemarks.classList.add('is-invalid', 'border-red-500', 'bg-red-50');
            teacherRemarks.classList.remove('border-gray-300');
            if (remarksErrorClient) remarksErrorClient.classList.remove('hidden');
            isValid = false;
        }

        // If status is COMPLETED, require recommended level
        if (reviewStatus && reviewStatus.value === 'COMPLETED') {
            if (recommendedLevel && !recommendedLevel.value) {
                recommendedLevel.classList.add('is-invalid', 'border-red-500');
                isValid = false;

                // Add validation message
                var feedback = document.createElement('p');
                feedback.className = 'mt-1 text-sm text-red-600 recommended-level-error';
                feedback.textContent = 'Level rekomendasi wajib dipilih untuk review yang selesai';
                recommendedLevel.parentElement.appendChild(feedback);
            }
        }

        if (!isValid) {
            // Scroll to first error field
            var firstErrorField = document.querySelector('.is-invalid');
            if (firstErrorField) {
                firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstErrorField.focus();
            }

            e.preventDefault();
            return false;
        }
    });

    // Save draft functionality
    if (saveDraftBtn) {
        saveDraftBtn.addEventListener('click', function(e) {
            if (reviewStatus) reviewStatus.value = 'IN_REVIEW';
            form.submit();
        });
    }

    // Character count for remarks
    if (teacherRemarks) {
        teacherRemarks.addEventListener('input', function() {
            var length = this.value.length;
            var feedback = this.parentElement.querySelector('.char-count');

            if (!feedback) {
                feedback = document.createElement('div');
                feedback.className = 'char-count form-text';
                this.parentElement.appendChild(feedback);
            }

            feedback.textContent = length + ' karakter';
            feedback.className = length >= 10 ? 'char-count form-text text-success' : 'char-count form-text text-muted';
        });

        // Trigger initial character count
        teacherRemarks.dispatchEvent(new Event('input'));
    }

    // Auto-save draft every 2 minutes (if in draft mode)
    var autoSaveInterval;
    if (reviewStatus) {
        reviewStatus.addEventListener('change', function() {
            if (this.value === 'IN_REVIEW') {
                // Start auto-save for drafts
                autoSaveInterval = setInterval(function() {
                    if (teacherRemarks && teacherRemarks.value.trim().length >= 10) {
                        // Auto-save logic could be implemented here
                        console.log('Auto-saving draft...');
                    }
                }, 120000); // 2 minutes
            } else {
                // Stop auto-save for final submission
                if (autoSaveInterval) {
                    clearInterval(autoSaveInterval);
                }
            }
        });
    }
});
