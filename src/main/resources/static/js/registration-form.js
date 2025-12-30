/**
 * Registration Form JavaScript
 * Extracted from inline script for CSP compliance
 */

document.addEventListener('DOMContentLoaded', function() {
    // Expose clearForm to window for tests
    window.clearRegistrationForm = clearForm;

    // Toggle tahsin details based on previous experience
    const tahsinRadios = document.querySelectorAll('input[name="previousTahsinExperience"]');
    const tahsinDetailsContainer = document.getElementById('tahsinDetailsContainer');

    if (tahsinRadios && tahsinDetailsContainer) {
        tahsinRadios.forEach(radio => {
            radio.addEventListener('change', function() {
                if (this.value === 'true') {
                    tahsinDetailsContainer.style.display = 'block';
                } else {
                    tahsinDetailsContainer.style.display = 'none';
                    const detailsField = document.getElementById('previousTahsinDetails');
                    if (detailsField) detailsField.value = '';
                }
            });
        });

        // Initialize display based on current value
        const checkedRadio = document.querySelector('input[name="previousTahsinExperience"]:checked');
        if (checkedRadio && checkedRadio.value === 'true') {
            tahsinDetailsContainer.style.display = 'block';
        }
    }

    // Handle day checkbox styling
    document.querySelectorAll('.day-checkbox input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const container = this.closest('.day-checkbox');
            if (this.checked) {
                container.classList.add('selected');
            } else {
                container.classList.remove('selected');
            }
        });

        // Initialize styling
        if (checkbox.checked) {
            checkbox.closest('.day-checkbox').classList.add('selected');
        }
    });

    // Session preference management
    let preferenceCount = document.querySelectorAll('.session-preference').length || 1;

    const addPreferenceBtn = document.getElementById('add-preference');
    if (addPreferenceBtn) {
        addPreferenceBtn.addEventListener('click', function() {
            if (preferenceCount >= 3) {
                alert('Maksimal 3 preferensi sesi');
                return;
            }

            addSessionPreference();
            preferenceCount++;
            updateAddButtonVisibility();
        });
    }

    function addSessionPreference() {
        const container = document.getElementById('session-preferences-container');
        const index = preferenceCount;

        const preferenceHtml = `
            <div id="session-pref-${index}" class="session-preference">
                <div class="session-preference-header">
                    <h4 class="font-semibold text-gray-800">Preferensi ${index + 1}</h4>
                    <div class="flex items-center space-x-2">
                        <span class="priority-badge priority-${index + 1}">Prioritas ${index + 1}</span>
                        <button type="button" class="text-red-500 hover:text-red-700 remove-preference">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="sessionPreferences${index}.sessionId" class="block text-sm font-medium text-gray-700 mb-1">
                            Sesi <span class="text-red-500">*</span>
                        </label>
                        <select name="sessionPreferences[${index}].sessionId" id="sessionPreferences${index}.sessionId"
                                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent session-select">
                            <option value="">Pilih sesi</option>
                            ${getSessionOptions()}
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="sessionPreferences${index}.priority" class="block text-sm font-medium text-gray-700 mb-1">
                            Prioritas <span class="text-red-500">*</span>
                        </label>
                        <select name="sessionPreferences[${index}].priority" id="sessionPreferences${index}.priority"
                                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent priority-select">
                            <option value="">Pilih prioritas</option>
                            <option value="1">1 (Tertinggi)</option>
                            <option value="2">2 (Sedang)</option>
                            <option value="3">3 (Terendah)</option>
                        </select>
                    </div>

                    <div class="form-group full-width">
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                            Hari yang Diinginkan <span class="text-red-500">*</span>
                        </label>
                        <div class="day-checkbox-group">
                            ${getDayCheckboxes(index)}
                        </div>
                    </div>
                </div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', preferenceHtml);

        // Add event listeners for new elements
        const newPreference = container.lastElementChild;
        addEventListenersToPreference(newPreference, index);
    }

    function getSessionOptions() {
        // Get options from first session select
        const firstSelect = document.querySelector('.session-select');
        if (firstSelect) {
            return firstSelect.innerHTML;
        }
        return '<option value="">Pilih sesi</option>';
    }

    function getDayCheckboxes(index) {
        const days = [
            {value: 'MONDAY', displayName: 'Senin'},
            {value: 'TUESDAY', displayName: 'Selasa'},
            {value: 'WEDNESDAY', displayName: 'Rabu'},
            {value: 'THURSDAY', displayName: 'Kamis'},
            {value: 'FRIDAY', displayName: 'Jumat'},
            {value: 'SATURDAY', displayName: 'Sabtu'},
            {value: 'SUNDAY', displayName: 'Minggu'}
        ];

        return days.map(day => `
            <div class="day-checkbox">
                <input type="checkbox" name="sessionPreferences[${index}].preferredDays" value="${day.value}" id="day_${index}_${day.value}">
                <label for="day_${index}_${day.value}" class="ml-2 text-sm">${day.displayName}</label>
            </div>
        `).join('');
    }

    function addEventListenersToPreference(preference, index) {
        // Remove button
        const removeBtn = preference.querySelector('.remove-preference');
        if (removeBtn) {
            removeBtn.addEventListener('click', function() {
                preference.remove();
                preferenceCount--;
                updateAddButtonVisibility();
                reindexPreferences();
            });
        }

        // Day checkboxes
        preference.querySelectorAll('.day-checkbox input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const container = this.closest('.day-checkbox');
                if (this.checked) {
                    container.classList.add('selected');
                } else {
                    container.classList.remove('selected');
                }
            });
        });
    }

    function updateAddButtonVisibility() {
        const addButton = document.getElementById('add-preference');
        if (addButton) {
            addButton.style.display = preferenceCount >= 3 ? 'none' : 'inline-flex';
        }
    }

    function reindexPreferences() {
        const preferences = document.querySelectorAll('.session-preference');
        preferences.forEach((pref, index) => {
            // Update visual labels
            const headerTitle = pref.querySelector('.session-preference-header h4');
            if (headerTitle) {
                headerTitle.textContent = `Preferensi ${index + 1}`;
            }

            // Update form field names and IDs
            const inputs = pref.querySelectorAll('input, select');
            inputs.forEach(input => {
                const name = input.getAttribute('name');
                const id = input.getAttribute('id');

                if (name) {
                    input.setAttribute('name', name.replace(/\[\d+\]/, `[${index}]`));
                }
                if (id) {
                    input.setAttribute('id', id.replace(/\d+/, index));
                }
            });

            // Update labels
            const labels = pref.querySelectorAll('label');
            labels.forEach(label => {
                const forAttr = label.getAttribute('for');
                if (forAttr) {
                    label.setAttribute('for', forAttr.replace(/\d+/, index));
                }
            });
        });

        preferenceCount = preferences.length;
    }

    // Initialize remove buttons for existing preferences
    document.querySelectorAll('.remove-preference').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.session-preference').remove();
            preferenceCount--;
            updateAddButtonVisibility();
            reindexPreferences();
        });
    });

    // Form submission handling
    const registrationForm = document.getElementById('registrationForm');
    if (registrationForm) {
        registrationForm.addEventListener('submit', function(e) {
            const submitText = document.querySelector('.submit-text');
            const loading = document.querySelector('.loading');

            if (submitText) submitText.style.display = 'none';
            if (loading) loading.style.display = 'inline-flex';

            // Disable submit button
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) submitBtn.disabled = true;
        });
    }

    // Initialize add button visibility
    updateAddButtonVisibility();

    // Step navigation implementation
    let currentStep = 1;
    const totalSteps = 5;

    // Initialize step navigation
    initializeStepNavigation();
    showStep(currentStep);

    function initializeStepNavigation() {
        // Add click handlers to step indicators
        document.querySelectorAll('.step').forEach((step, index) => {
            step.style.cursor = 'pointer';
            step.addEventListener('click', function() {
                const targetStep = index + 1;
                if (targetStep <= currentStep || validateCurrentStep()) {
                    goToStep(targetStep);
                }
            });
        });

        // Add navigation buttons to each section
        addNavigationButtons();
    }

    function addNavigationButtons() {
        const sections = [
            'section-personal',
            'section-education',
            'section-program',
            'section-schedule',
            'section-placement'
        ];

        sections.forEach((sectionId, index) => {
            const section = document.getElementById(sectionId);
            if (section && index < sections.length - 1) {
                // Check if nav buttons already exist
                if (section.querySelector('.step-navigation')) return;

                const navDiv = document.createElement('div');
                navDiv.className = 'step-navigation mt-6 flex justify-between';

                if (index > 0) {
                    const prevBtn = document.createElement('button');
                    prevBtn.type = 'button';
                    prevBtn.id = `prev-btn-step-${index + 1}`;
                    prevBtn.className = 'px-4 py-2 border border-gray-300 rounded-md text-gray-700 bg-white hover:bg-gray-50';
                    prevBtn.innerHTML = '<i class="fas fa-arrow-left mr-2"></i>Sebelumnya';
                    prevBtn.addEventListener('click', () => goToStep(index));
                    navDiv.appendChild(prevBtn);
                }

                const nextBtn = document.createElement('button');
                nextBtn.type = 'button';
                nextBtn.id = `next-btn-step-${index + 1}`;
                nextBtn.className = 'px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 ml-auto';
                nextBtn.innerHTML = 'Selanjutnya<i class="fas fa-arrow-right ml-2"></i>';
                nextBtn.addEventListener('click', () => {
                    if (validateCurrentStep()) {
                        saveProgress();
                        goToStep(index + 2);
                    }
                });
                navDiv.appendChild(nextBtn);

                section.appendChild(navDiv);
            }
        });
    }

    function goToStep(step) {
        if (step >= 1 && step <= totalSteps) {
            currentStep = step;
            showStep(step);
            updateStepIndicators();
            saveProgress();

            // Scroll to top
            window.scrollTo(0, 0);
        }
    }

    function showStep(step) {
        const sections = [
            'section-personal',
            'section-education',
            'section-program',
            'section-schedule',
            'section-placement'
        ];

        sections.forEach((sectionId, index) => {
            const section = document.getElementById(sectionId);
            if (section) {
                section.style.display = (index + 1 === step) ? 'block' : 'none';
            }
        });

        // Show/hide submit section based on last step
        const submitSection = document.querySelector('.form-section:last-of-type');
        if (submitSection) {
            submitSection.style.display = step === totalSteps ? 'block' : 'none';
        }
    }

    function updateStepIndicators() {
        document.querySelectorAll('.step').forEach((step, index) => {
            step.classList.toggle('active', index + 1 === currentStep);
            step.classList.toggle('completed', index + 1 < currentStep);
        });
    }

    function validateCurrentStep() {
        const currentSection = getCurrentSection();
        if (!currentSection) return true;

        const requiredFields = currentSection.querySelectorAll('[required], input[type="email"], input[type="tel"]');
        let isValid = true;

        requiredFields.forEach(field => {
            field.classList.remove('border-red-500');

            if (!field.value.trim()) {
                field.classList.add('border-red-500');
                isValid = false;
            }

            if (field.type === 'email' && field.value.trim()) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(field.value.trim())) {
                    field.classList.add('border-red-500');
                    isValid = false;
                }
            }
        });

        if (currentStep === 4) {
            const sessionSelects = currentSection.querySelectorAll('.session-select');
            let hasValidSession = false;
            sessionSelects.forEach(select => {
                if (select.value.trim()) hasValidSession = true;
            });
            if (!hasValidSession) {
                isValid = false;
                alert('Pilih minimal satu preferensi sesi');
            }
        }

        if (!isValid) {
            alert('Mohon lengkapi semua field yang wajib diisi sebelum melanjutkan ke step berikutnya');
        }

        return isValid;
    }

    function getCurrentSection() {
        const sections = [
            'section-personal',
            'section-education',
            'section-program',
            'section-schedule',
            'section-placement'
        ];

        if (currentStep >= 1 && currentStep <= sections.length) {
            return document.getElementById(sections[currentStep - 1]);
        }
        return null;
    }

    function saveProgress() {
        const form = document.getElementById('registrationForm');
        if (!form) return;

        const formData = new FormData(form);
        const progressData = {
            currentStep: currentStep,
            formData: Object.fromEntries(formData),
            timestamp: new Date().toISOString()
        };

        localStorage.setItem('registrationProgress', JSON.stringify(progressData));
    }

    function loadProgress() {
        try {
            const saved = localStorage.getItem('registrationProgress');
            if (saved) {
                const progressData = JSON.parse(saved);

                Object.entries(progressData.formData).forEach(([name, value]) => {
                    const field = document.querySelector(`[name="${name}"]`);
                    if (field) {
                        if (field.type === 'checkbox' || field.type === 'radio') {
                            field.checked = field.value === value;
                        } else {
                            field.value = value;
                        }
                    }
                });

                if (progressData.currentStep) {
                    currentStep = progressData.currentStep;
                    showStep(currentStep);
                    updateStepIndicators();
                }
            }
        } catch (e) {
            console.log('No saved progress found');
        }
    }

    loadProgress();

    if (registrationForm) {
        registrationForm.addEventListener('submit', function() {
            localStorage.removeItem('registrationProgress');
        });
    }

    // Clear form functionality
    const clearFormBtn = document.getElementById('clearFormBtn');
    if (clearFormBtn) {
        clearFormBtn.addEventListener('click', function() {
            showClearFormConfirmation();
        });
    }

    // Manual save progress functionality
    const saveProgressBtn = document.getElementById('saveProgressBtn');
    if (saveProgressBtn) {
        saveProgressBtn.addEventListener('click', function() {
            saveProgress();
            showSaveConfirmation();
        });
    }

    function showClearFormConfirmation() {
        const confirmClear = confirm(
            'Apakah Anda yakin ingin menghapus semua data yang telah diisi?\n\n' +
            'Tindakan ini akan:\n' +
            '• Menghapus semua data formulir\n' +
            '• Menghapus progress yang tersimpan\n' +
            '• Mengembalikan ke langkah pertama\n\n' +
            'Tindakan ini tidak dapat dibatalkan.'
        );

        if (confirmClear) {
            clearForm();
        }
    }

    function clearForm() {
        // Clear localStorage
        localStorage.removeItem('registrationProgress');

        // Reset form
        const form = document.getElementById('registrationForm');
        if (form) form.reset();

        // Clear session preferences (dynamic content)
        const sessionContainer = document.getElementById('session-preferences-container');
        if (sessionContainer) {
            const preferences = sessionContainer.querySelectorAll('.session-preference');
            for (let i = 1; i < preferences.length; i++) {
                preferences[i].remove();
            }

            const firstPref = sessionContainer.querySelector('.session-preference');
            if (firstPref) {
                firstPref.querySelectorAll('select, input').forEach(field => {
                    if (field.type === 'checkbox') {
                        field.checked = false;
                    } else {
                        field.value = '';
                    }
                });
            }
        }

        // Reset step navigation
        currentStep = 1;
        preferenceCount = 1;
        showStep(1);
        updateStepIndicators();

        window.scrollTo(0, 0);
        showClearConfirmation();
    }

    function showClearConfirmation() {
        const successDiv = document.createElement('div');
        successDiv.className = 'bg-green-50 border-l-4 border-green-400 text-green-700 px-4 py-3 rounded mb-6 transition-opacity duration-500';
        successDiv.innerHTML = `
            <div class="flex items-center">
                <i class="fas fa-check-circle mr-2"></i>
                <span>Form berhasil dibersihkan. Anda dapat memulai pengisian dari awal.</span>
            </div>
        `;

        const formControls = document.querySelector('.form-controls');
        if (formControls) {
            formControls.parentNode.insertBefore(successDiv, formControls.nextSibling);
        }

        setTimeout(() => {
            successDiv.style.opacity = '0';
            setTimeout(() => {
                if (successDiv.parentNode) {
                    successDiv.parentNode.removeChild(successDiv);
                }
            }, 500);
        }, 5000);
    }

    function showSaveConfirmation() {
        const successDiv = document.createElement('div');
        successDiv.className = 'bg-blue-50 border-l-4 border-blue-400 text-blue-700 px-4 py-3 rounded mb-6 transition-opacity duration-500';
        successDiv.innerHTML = `
            <div class="flex items-center">
                <i class="fas fa-save mr-2"></i>
                <span>Progress berhasil disimpan. Anda dapat melanjutkan pengisian nanti.</span>
            </div>
        `;

        const formControls = document.querySelector('.form-controls');
        if (formControls) {
            formControls.parentNode.insertBefore(successDiv, formControls.nextSibling);
        }

        setTimeout(() => {
            successDiv.style.opacity = '0';
            setTimeout(() => {
                if (successDiv.parentNode) {
                    successDiv.parentNode.removeChild(successDiv);
                }
            }, 500);
        }, 5000);
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 's') {
            e.preventDefault();
            saveProgress();
            showSaveConfirmation();
        }

        if (e.ctrlKey && e.shiftKey && e.key === 'Delete') {
            e.preventDefault();
            showClearFormConfirmation();
        }

        if (!['INPUT', 'TEXTAREA', 'SELECT'].includes(e.target.tagName)) {
            if (e.key === 'ArrowLeft' && currentStep > 1) {
                e.preventDefault();
                goToStep(currentStep - 1);
            } else if (e.key === 'ArrowRight' && currentStep < totalSteps) {
                e.preventDefault();
                if (validateCurrentStep()) {
                    goToStep(currentStep + 1);
                }
            }
        }
    });
});
