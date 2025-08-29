-- =====================================================
-- STUDENT REGISTRATION SYSTEM SEED DATA
-- Version: V004__student_registration_seed_data.sql
-- Description: Reference data for student registration
-- =====================================================

-- =====================================================
-- 1. INSERT PROGRAMS
-- =====================================================
INSERT INTO programs (id, code, name, description, level_order, is_active) VALUES
('80000000-0000-0000-0000-000000000001', 'TAHSIN_1', 'Tahsin Level 1', 'Program tahsin dasar untuk pemula yang belum bisa membaca Al-Quran dengan baik', 1, true),
('80000000-0000-0000-0000-000000000002', 'TAHSIN_2', 'Tahsin Level 2', 'Program tahsin lanjutan untuk yang sudah bisa membaca Al-Quran namun perlu perbaikan makhorijul huruf', 2, true),
('80000000-0000-0000-0000-000000000003', 'TAHSIN_3', 'Tahsin Level 3', 'Program tahsin mahir untuk penyempurnaan bacaan dan penguasaan tajwid', 3, true),
('80000000-0000-0000-0000-000000000004', 'TAHFIDZ_PEMULA', 'Tahfidz Pemula', 'Program menghafal Al-Quran untuk pemula, target 1-5 juz', 4, true),
('80000000-0000-0000-0000-000000000005', 'TAHFIDZ_LANJUTAN', 'Tahfidz Lanjutan', 'Program menghafal Al-Quran lanjutan, target 6-15 juz', 5, true),
('80000000-0000-0000-0000-000000000006', 'TAHFIDZ_TINGGI', 'Tahfidz Tinggi', 'Program menghafal Al-Quran tingkat tinggi, target 16-30 juz', 6, true);

-- =====================================================
-- 2. INSERT SESSIONS (SESI 1-7 as requested)
-- =====================================================
INSERT INTO sessions (id, code, name, start_time, end_time, is_active) VALUES
('90000000-0000-0000-0000-000000000001', 'SESI_1', 'Sesi 1 (07-09)', '07:00:00', '09:00:00', true),
('90000000-0000-0000-0000-000000000002', 'SESI_2', 'Sesi 2 (08-10)', '08:00:00', '10:00:00', true),
('90000000-0000-0000-0000-000000000003', 'SESI_3', 'Sesi 3 (09-11)', '09:00:00', '11:00:00', true),
('90000000-0000-0000-0000-000000000004', 'SESI_4', 'Sesi 4 (10-12)', '10:00:00', '12:00:00', true),
('90000000-0000-0000-0000-000000000005', 'SESI_5', 'Sesi 5 (13-15)', '13:00:00', '15:00:00', true),
('90000000-0000-0000-0000-000000000006', 'SESI_6', 'Sesi 6 (14-16)', '14:00:00', '16:00:00', true),
('90000000-0000-0000-0000-000000000007', 'SESI_7', 'Sesi 7 (15-17)', '15:00:00', '17:00:00', true);

-- =====================================================
-- 3. INSERT PLACEMENT TEST VERSES
-- =====================================================
-- Easy verses (Difficulty Level 1)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, transliteration, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000001', 1, 'Al-Fatihah', 1, 7, 
'بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ﴿١﴾ ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ ﴿٢﴾ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ﴿٣﴾ مَٰلِكِ يَوْمِ ٱلدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ ﴿٦﴾ صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ ﴿٧﴾',
'Bismillahir-rahmanir-raheem. Alhamdulillahi rabbil-alameen. Ar-rahmanir-raheem. Maliki yawmid-deen. Iyyaka na''budu wa iyyaka nasta''een. Ihdinash-shiratal-mustaqeem. Shirat alladhina an''amta alayhim ghayril-maghdubi alayhim wa lad-dalleen.',
1, true),

('A0000000-0000-0000-0000-000000000002', 112, 'Al-Ikhlas', 1, 4, 
'قُلْ هُوَ ٱللَّهُ أَحَدٌ ﴿١﴾ ٱللَّهُ ٱلصَّمَدُ ﴿٢﴾ لَمْ يَلِدْ وَلَمْ يُولَدْ ﴿٣﴾ وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ ﴿٤﴾',
'Qul huwallahu ahad. Allahush-shamad. Lam yalid wa lam yuulad. Wa lam yakun lahu kufuwan ahad.',
1, true),

('A0000000-0000-0000-0000-000000000003', 113, 'Al-Falaq', 1, 5, 
'قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ ﴿١﴾ مِن شَرِّ مَا خَلَقَ ﴿٢﴾ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ﴿٣﴾ وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ ﴿٤﴾ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ﴿٥﴾',
'Qul a''udhu birabbilfalaqi. Min sharri ma khalaq. Wa min sharri ghasiqin idha waqab. Wa min sharri naffathati fil-''uqad. Wa min sharri hasidin idha hasad.',
1, true);

-- Medium verses (Difficulty Level 2)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, transliteration, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000004', 2, 'Al-Baqarah', 1, 5, 
'ٱلٓمٓ ﴿١﴾ ذَٰلِكَ ٱلْكِتَٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ ﴿٢﴾ ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَٰهُمْ يُنفِقُونَ ﴿٣﴾ وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْءَاخِرَةِ هُمْ يُوقِنُونَ ﴿٤﴾ أُولَٰٓئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ ﴿٥﴾',
'Alif-lam-meem. Dhalikal-kitabu la rayba feeh, hudan lil-muttaqeen. Alladheena yu''minoona bil-ghaybi wa yuqeemoona ash-shalata wa mimma razaqnahum yunfiqoon. Walladheena yu''minoona bima unzila ilayka wa ma unzila min qablika wa bil-akhirati hum yooqinoon. Ulaika ''ala hudan min rabbihim wa ulaika humul-muflihoon.',
2, true),

('A0000000-0000-0000-0000-000000000005', 36, 'Ya-Sin', 1, 5, 
'يٓسٓ ﴿١﴾ وَٱلْقُرْءَانِ ٱلْحَكِيمِ ﴿٢﴾ إِنَّكَ لَمِنَ ٱلْمُرْسَلِينَ ﴿٣﴾ عَلَىٰ صِرَٰطٍ مُّسْتَقِيمٍ ﴿٤﴾ تَنزِيلَ ٱلْعَزِيزِ ٱلرَّحِيمِ ﴿٥﴾',
'Ya-seen. Wal-quranil-hakeem. Innaka laminal-mursaleen. ''Ala shiratin mustaqeem. Tanzeelul-''azeezir-raheem.',
2, true);

-- Hard verses (Difficulty Level 3)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, transliteration, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000006', 2, 'Al-Baqarah', 255, 255, 
'ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ ۚ لَا تَأْخُذُهُۥ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُۥ مَا فِى ٱلسَّمَٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ۗ مَن ذَا ٱلَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَىْءٍ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ ۚ وَسِعَ كُرْسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلْأَرْضَ ۖ وَلَا يَـُٔودُهُۥ حِفْظُهُمَا ۚ وَهُوَ ٱلْعَلِىُّ ٱلْعَظِيمُ',
'Allahu la ilaha illa huwal-hayyul-qayyoom. La ta''khudhuhuu sinatun wa la nawm. Lahuu ma fis-samawati wa ma fil-ard. Man dhal-ladhee yashfa''u ''indahuu illa bi-idhnih. Ya''lamu ma bayna aydeehim wa ma khalfahum wa la yuheetoona bi-shay''in min ''ilmihee illa bima sha''a. Wasi''a kursiyyuhus-samawati wal-ard wa la ya''oouduhuu hifdhuhuma wa huwal-''aliyyul-''adheem.',
3, true),

('A0000000-0000-0000-0000-000000000007', 18, 'Al-Kahf', 1, 3, 
'ٱلْحَمْدُ لِلَّهِ ٱلَّذِىٓ أَنزَلَ عَلَىٰ عَبْدِهِ ٱلْكِتَٰبَ وَلَمْ يَجْعَل لَّهُۥ عِوَجًا ۜ ﴿١﴾ قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ ٱلْمُؤْمِنِينَ ٱلَّذِينَ يَعْمَلُونَ ٱلصَّٰلِحَٰتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا ﴿٢﴾ مَّٰكِثِينَ فِيهِ أَبَدًا ﴿٣﴾',
'Alhamdu lillahil-ladhee anzala ''ala ''abdihil-kitaba wa lam yaj''al lahuu ''iwajan. Qayyiman li-yundhira ba''san shadeedan min ladunhu wa yubashshiral-mu''mineena alladheena ya''maloonas-shalihati anna lahum ajran hasanan. Makitheena feehi abadan.',
3, true);

-- Very Hard verses (Difficulty Level 4)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, transliteration, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000008', 17, 'Al-Isra', 110, 111, 
'قُلِ ٱدْعُوا۟ ٱللَّهَ أَوِ ٱدْعُوا۟ ٱلرَّحْمَٰنَ ۖ أَيًّا مَّا تَدْعُوا۟ فَلَهُ ٱلْأَسْمَآءُ ٱلْحُسْنَىٰ ۚ وَلَا تَجْهَرْ بِصَلَاتِكَ وَلَا تُخَافِتْ بِهَا وَٱبْتَغِ بَيْنَ ذَٰلِكَ سَبِيلًا ﴿١١٠﴾ وَقُلِ ٱلْحَمْدُ لِلَّهِ ٱلَّذِى لَمْ يَتَّخِذْ وَلَدًا وَلَمْ يَكُن لَّهُۥ شَرِيكٌ فِى ٱلْمُلْكِ وَلَمْ يَكُن لَّهُۥ وَلِىٌّ مِّنَ ٱلذُّلِّ ۖ وَكَبِّرْهُ تَكْبِيرًا ﴿١١١﴾',
'Qulid-''ullaha awid-''ur-rahman. Ayyan ma tad-''uu falahul-asma''ul-husna. Wa la tajhar bi-shalatika wa la tukhafir biha wabtagh bayna dhalika sabeela. Wa qulil-hamdu lillahil-ladhee lam yattakhidh waladan wa lam yakun lahuu shareekun fil-mulki wa lam yakun lahuu waliyyun minnadh-dhulli wa kabbirhu takbeera.',
4, true);

-- Expert verses (Difficulty Level 5)
INSERT INTO placement_test_verses (id, surah_number, surah_name, ayah_start, ayah_end, arabic_text, transliteration, difficulty_level, is_active) VALUES
('A0000000-0000-0000-0000-000000000009', 33, 'Al-Ahzab', 35, 35, 
'إِنَّ ٱلْمُسْلِمِينَ وَٱلْمُسْلِمَٰتِ وَٱلْمُؤْمِنِينَ وَٱلْمُؤْمِنَٰتِ وَٱلْقَٰنِتِينَ وَٱلْقَٰنِتَٰتِ وَٱلصَّٰدِقِينَ وَٱلصَّٰدِقَٰتِ وَٱلصَّٰبِرِينَ وَٱلصَّٰبِرَٰتِ وَٱلْخَٰشِعِينَ وَٱلْخَٰشِعَٰتِ وَٱلْمُتَصَدِّقِينَ وَٱلْمُتَصَدِّقَٰتِ وَٱلصَّٰٓئِمِينَ وَٱلصَّٰٓئِمَٰتِ وَٱلْحَٰفِظِينَ فُرُوجَهُمْ وَٱلْحَٰفِظَٰتِ وَٱلذَّٰكِرِينَ ٱللَّهَ كَثِيرًا وَٱلذَّٰكِرَٰتِ أَعَدَّ ٱللَّهُ لَهُم مَّغْفِرَةً وَأَجْرًا عَظِيمًا',
'Innal-muslimeena wal-muslimati wal-mu''mineena wal-mu''minati wal-qaniteena wal-qanitati wash-shadiqeena wash-shadiqati wash-shabireena wash-shabirat wal-khashi''eena wal-khashi''ati wal-mutasaddiqeena wal-mutasaddiqati wash-sha''imeena wash-sha''imati wal-hafidheena furujahum wal-hafidhat wadh-dhakireena allaha katheeran wadh-dhakirati a''addallahu lahum maghfiratan wa ajran ''adheeman.',
5, true);

-- =====================================================
-- 4. ADD STUDENT REGISTRATION PERMISSIONS
-- =====================================================
INSERT INTO permissions (code, name, module) VALUES
('STUDENT_REG_VIEW', 'View Student Registrations', 'STUDENT_REGISTRATION'),
('STUDENT_REG_CREATE', 'Create Student Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_EDIT', 'Edit Student Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_ASSIGN_TEACHER', 'Assign Teacher to Review Registration', 'STUDENT_REGISTRATION'),
('STUDENT_REG_REVIEW', 'Review Student Registration (Teacher)', 'STUDENT_REGISTRATION'),
('STUDENT_REG_EVALUATE', 'Evaluate and Set Level (Teacher)', 'STUDENT_REGISTRATION'),
('PLACEMENT_TEST_EVALUATE', 'Evaluate Placement Test', 'STUDENT_REGISTRATION'),
('PLACEMENT_TEST_MANAGE', 'Manage Placement Test Content', 'STUDENT_REGISTRATION'),
('STUDENT_REG_REPORT', 'View Registration Reports', 'STUDENT_REGISTRATION');

-- =====================================================
-- 5. ASSIGN STUDENT REGISTRATION PERMISSIONS TO ROLES
-- =====================================================
-- ADMIN (System Administrator) - NO operational permissions for registration
-- Only gets system management permissions (handled in V002)
-- Removing registration permissions from ADMIN role

-- INSTRUCTOR/TEACHER gets review and evaluation permissions for assigned registrations
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000002', id FROM permissions
WHERE code IN ('STUDENT_REG_VIEW', 'STUDENT_REG_REVIEW', 'STUDENT_REG_EVALUATE', 'PLACEMENT_TEST_EVALUATE');

-- ADMIN_STAFF gets registration management and teacher assignment permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000004', id FROM permissions
WHERE code IN (
    'STUDENT_REG_VIEW', 'STUDENT_REG_CREATE', 'STUDENT_REG_EDIT', 
    'STUDENT_REG_ASSIGN_TEACHER', 'STUDENT_REG_REPORT'
);

-- MANAGEMENT gets view, reporting, and teacher assignment permissions
INSERT INTO role_permissions (id_role, id_permission)
SELECT '10000000-0000-0000-0000-000000000006', id FROM permissions
WHERE code IN ('STUDENT_REG_VIEW', 'STUDENT_REG_ASSIGN_TEACHER', 'STUDENT_REG_REPORT');

-- =====================================================
-- 6. INSERT SAMPLE STUDENT REGISTRATION DATA
-- =====================================================
-- Sample registration in SUBMITTED status
INSERT INTO student_registrations (
    id, full_name, gender, date_of_birth, place_of_birth, phone_number, email, address,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    education_level, school_name, quran_reading_experience, previous_tahsin_experience,
    id_program, registration_reason, learning_goals, schedule_preferences,
    registration_status, submitted_at
) VALUES (
    'B0000000-0000-0000-0000-000000000001',
    'Ahmad Zaki Mubarak',
    'MALE',
    '1995-03-15',
    'Jakarta',
    '081234567890',
    'ahmad.zaki@email.com',
    'Jl. Raya Bogor No. 123, Jakarta Timur',
    'Fatimah Zaki',
    '081234567891',
    'Istri',
    'S1',
    'Universitas Indonesia',
    'Sudah bisa membaca Al-Quran sejak kecil, namun merasa perlu perbaikan makhorijul huruf',
    true,
    '80000000-0000-0000-0000-000000000001',
    'Ingin memperbaiki bacaan Al-Quran dan mempersiapkan diri untuk tahfidz',
    'Menguasai tajwid dengan baik dan bisa membaca dengan fasih',
    '[{"session_id": "90000000-0000-0000-0000-000000000002", "priority": 1, "days": ["MONDAY", "WEDNESDAY"]}, {"session_id": "90000000-0000-0000-0000-000000000005", "priority": 2, "days": ["TUESDAY", "THURSDAY"]}]',
    'SUBMITTED',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);

-- Insert corresponding session preferences
INSERT INTO student_session_preferences (id, id_registration, id_session, preference_priority, preferred_days) VALUES
('C0000000-0000-0000-0000-000000000001', 'B0000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000002', 1, '["MONDAY", "WEDNESDAY"]'),
('C0000000-0000-0000-0000-000000000002', 'B0000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000005', 2, '["TUESDAY", "THURSDAY"]');

-- Add placement test assignment
UPDATE student_registrations 
SET id_placement_verse = 'A0000000-0000-0000-0000-000000000004',
    placement_test_status = 'PENDING'
WHERE id = 'B0000000-0000-0000-0000-000000000001';

-- =====================================================
-- 7. ADD TEACHER ASSIGNMENT FIELDS TO STUDENT REGISTRATION
-- =====================================================
-- Add teacher assignment fields to student_registrations table
ALTER TABLE student_registrations 
ADD COLUMN assigned_teacher_id UUID REFERENCES users(id),
ADD COLUMN assigned_at TIMESTAMP,
ADD COLUMN assigned_by_id UUID REFERENCES users(id),
ADD COLUMN teacher_review_status VARCHAR(50) DEFAULT 'PENDING',
ADD COLUMN teacher_remarks TEXT,
ADD COLUMN recommended_level_id UUID REFERENCES programs(id),
ADD COLUMN teacher_evaluated_at TIMESTAMP;

-- Add check constraint for teacher review status
ALTER TABLE student_registrations
ADD CONSTRAINT chk_teacher_review_status 
CHECK (teacher_review_status IN ('PENDING', 'IN_REVIEW', 'COMPLETED'));

-- Update registration status constraint to reflect new workflow
ALTER TABLE student_registrations DROP CONSTRAINT IF EXISTS student_registrations_registration_status_check;
ALTER TABLE student_registrations 
ADD CONSTRAINT student_registrations_registration_status_check 
CHECK (registration_status IN ('DRAFT', 'SUBMITTED', 'ASSIGNED', 'REVIEWED', 'COMPLETED', 'REJECTED'));

-- Create audit table for teacher assignments
CREATE TABLE teacher_assignment_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    registration_id UUID NOT NULL REFERENCES student_registrations(id),
    assigned_teacher_id UUID REFERENCES users(id),
    assigned_by_id UUID REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample update for existing test data - assign teacher to submitted registration
UPDATE student_registrations
SET assigned_teacher_id = '20000000-0000-0000-0000-000000000001', -- ustadz.ahmad
    assigned_at = CURRENT_TIMESTAMP,
    assigned_by_id = '40000000-0000-0000-0000-000000000001', -- staff.admin1
    registration_status = 'ASSIGNED'
WHERE id = 'B0000000-0000-0000-0000-000000000001'
AND registration_status = 'SUBMITTED';

-- =====================================================
-- 8. CREATE INDEXES FOR PERFORMANCE
-- =====================================================
-- Additional indexes for JSONB columns
CREATE INDEX idx_student_reg_schedule_prefs ON student_registrations USING GIN (schedule_preferences);
CREATE INDEX idx_session_pref_days ON student_session_preferences USING GIN (preferred_days);

-- Teacher assignment indexes
CREATE INDEX idx_student_reg_assigned_teacher ON student_registrations(assigned_teacher_id);
CREATE INDEX idx_student_reg_teacher_review_status ON student_registrations(teacher_review_status);

-- Comments for new fields
COMMENT ON COLUMN student_registrations.registration_status IS 'Overall registration status: DRAFT, SUBMITTED, ASSIGNED, REVIEWED, COMPLETED, REJECTED';
COMMENT ON COLUMN student_registrations.teacher_review_status IS 'Teacher review progress: PENDING, IN_REVIEW, COMPLETED';
COMMENT ON COLUMN student_registrations.assigned_teacher_id IS 'Teacher assigned to review this registration';
COMMENT ON COLUMN student_registrations.teacher_remarks IS 'Teacher evaluation remarks and feedback';
COMMENT ON COLUMN student_registrations.recommended_level_id IS 'Program level recommended by teacher after evaluation';