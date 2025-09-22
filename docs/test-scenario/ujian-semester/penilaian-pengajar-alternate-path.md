# Skenario Pengujian: Penilaian Ujian Pengajar - Alternate Path

## Informasi Umum
- **Kategori**: Ujian Semester - Penilaian Pengajar (Kasus Error)
- **Modul**: Penilaian Pengajar - Penanganan Error dan Edge Case
- **Tipe Skenario**: Alternate Path (Jalur Alternatif/Error)
- **Total Skenario**: 8 skenario penanganan error untuk proses penilaian pengajar
- **Playwright Test**: `teacher-grading.TeacherGradingAlternateTest`

---

## PP-AP-001: Pengajar - Banding Nilai & Scoring Disputed

### Informasi Skenario
- **ID Skenario**: PP-AP-001 (Penilaian Pengajar - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-18 menit
- **Tipe Error**: Integritas Akademik & Proses Banding

### Prasyarat
- Siswa telah mengajukan banding nilai
- Penilaian asli sudah selesai
- Proses review banding diperlukan
- Mekanisme second opinion tersedia

### Data Test
```
Skenario Banding:
Siswa: Fatima Zahra
Nilai Essay Asli: 7/12 poin
Klaim Banding: "Jawaban pantas skor lebih tinggi"
Isu Spesifik:
- Klaim rubrik salah diterapkan
- Percaya jawaban menunjukkan pemahaman lebih dalam
- Mempertanyakan konsistensi penilaian

Bukti Pendukung:
- Siswa menyediakan referensi tambahan
- Klaim jawaban serupa dapat skor lebih tinggi
- Meminta penjelasan detail

Kebutuhan Review:
- Re-evaluasi dengan perspektif fresh
- Bandingkan dengan respon serupa
- Berikan justifikasi detail
- Pertahankan standar akademik
```

### Langkah Pengujian

#### Bagian 1: Terima dan Proses Banding
1. **Handle Notifikasi Banding Nilai**
   - Aksi: Terima banding melalui sistem
   - Verifikasi:
     - Notifikasi banding prominent
     - Alasan siswa ditampilkan jelas
     - Jawaban asli dan nilai accessible
     - Histori aplikasi rubrik terlihat
     - Tracking deadline banding aktif

2. **Akses Interface Review Banding**
   - Aksi: Masuk mode review banding
   - Verifikasi:
     - View perbandingan side-by-side
     - Scoring rubrik asli terlihat
     - Bukti tambahan siswa shown
     - Opsi review anonymous tersedia
     - Requirement dokumentasi jelas

#### Bagian 2: Lakukan Re-evaluasi Fair
3. **Re-apply Rubrik Secara Objektif**
   - Aksi: Re-evaluasi essay menggunakan rubrik asli:
     - Ketepatan Definisi: Re-assess 2/3 → 3/3
     - Contoh Diberikan: Maintain 2/3
     - Ketepatan Arab: Maintain 3/3
     - Kejelasan: Re-assess 0/3 → 1/3
   - Verifikasi:
     - Dapat adjust skor kriteria individual
     - Harus provide justifikasi perubahan
     - Tracking scoring asli vs revisi
     - Warning deteksi bias aktif
     - Konsistensi kualitas terjaga

4. **Bandingkan dengan Respon Serupa**
   - Aksi: Review jawaban siswa yang comparable
   - Verifikasi:
     - Respon kualitas serupa accessible
     - Perbandingan ter-anonymized tersedia
     - Analisis konsistensi scoring
     - Distribusi statistik shown
     - Deteksi outlier aktif

#### Bagian 3: Resolusi dan Dokumentasi
5. **Buat Keputusan Banding**
   - Aksi: Tentukan outcome banding:
     - Nilai asli dipertahankan: 7/12
     - Adjustment parsial: 8/12
     - Banding penuh granted: 10/12
   - Untuk skenario ini: Adjustment parsial ke 8/12
   - Verifikasi:
     - Dokumentasi keputusan jelas
     - Reasoning detail diberikan
     - Notifikasi siswa otomatis
     - Perubahan nilai propagated
     - Audit trail maintained

6. **Berikan Feedback Edukatif**
   - Aksi: Buat respon komprehensif:
     ```
     "Setelah re-evaluasi teliti, banding Anda diterima sebagian.
     Nilai Anda disesuaikan dari 7/12 menjadi 8/12.

     Alasan:
     - Ketepatan definisi: Dinaikkan ke 3/3 (pemahaman excellent terbukti)
     - Contoh: Dipertahankan 2/3 (baik tapi incomplete)
     - Teks Arab: Dipertahankan 3/3 (akurat sepanjang jawaban)
     - Kejelasan: Sedikit improved ke 1/3 (organisasi masih perlu diperbaiki)

     Untuk peningkatan kedepan:
     - Sertakan contoh Ikhfa untuk melengkapi coverage
     - Struktur jawaban dengan heading yang jelas
     - Pemahaman Anda kuat, fokus pada presentasi"
     ```
   - Verifikasi:
     - Penjelasan kriteria demi kriteria detail
     - Panduan improvement konstruktif
     - Tone profesional dan respectful
     - Nilai edukatif dijaga
     - Martabat proses banding preserved

### Kriteria Sukses
- [ ] Banding handled fairly dan transparan
- [ ] Re-evaluasi unbiased dan thorough
- [ ] Dokumentasi komprehensif
- [ ] Siswa menerima feedback jelas
- [ ] Standar akademik maintained

---

## PP-AP-002: Pengajar - Deteksi Inkonsistensi Penilaian

### Informasi Skenario
- **ID Skenario**: PP-AP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 12-15 menit
- **Tipe Error**: Isu Quality Control

### Prasyarat
- Batch grading sudah selesai
- Analisis statistik reveal inkonsistensi
- Sistem quality control triggered
- Review administratif diperlukan

### Data Test
```
Alert Inkonsistensi:
Sesi Penilaian: Essay Midterm Q32
Siswa Dinilai: 20 siswa
Isu Terdeteksi: Outlier statistik dalam scoring

Analisis Statistik:
- Mean Score: 8.2/12
- Standar Deviasi: 2.1
- Outlier Terdeteksi: 3 skor
  * Siswa A: 12/12 (3+ std dev di atas mean)
  * Siswa B: 2/12 (3+ std dev di bawah mean)
  * Siswa C: 12/12 (konten serupa dengan jawaban 6/12)

Alert Kualitas: "Inkonsistensi scoring terdeteksi - Review diperlukan"
```

### Langkah Pengujian

#### Bagian 1: Deteksi Inkonsistensi
1. **Terima Alert Quality Control**
   - Aksi: Sistem flag grading inkonsisten
   - Verifikasi:
     - Notifikasi alert jelas
     - Analisis statistik shown
     - Kasus outlier highlighted
     - Requirement review dijelaskan
     - Timeline resolusi diberikan

#### Bagian 2: Review Skor yang Di-flag
2. **Investigasi Outlier Tinggi (Siswa A: 12/12)**
   - Aksi: Re-examine essay skor sempurna
   - Verifikasi:
     - Essay asli accessible
     - Scoring rubrik terlihat
     - Dapat compare dengan essay kualitas serupa
     - Justifikasi nilai diperlukan
     - Dapat maintain atau adjust skor

3. **Investigasi Outlier Rendah (Siswa B: 2/12)**
   - Aksi: Re-examine skor sangat rendah
   - Verifikasi:
     - Review konten essay
     - Cek kemungkinan error grading
     - Pertimbangan keadaan siswa
     - Opsi referral support akademik
     - Proses re-evaluasi fair

4. **Address Concern Similaritas (Siswa C vs referensi)**
   - Aksi: Compare konten serupa dengan skor berbeda
   - Verifikasi:
     - Perbandingan konten side-by-side
     - Analisis perbedaan kualitas
     - Konsistensi aplikasi rubrik
     - Dokumentasi justifikasi
     - Opsi harmonisasi skor

#### Bagian 3: Koreksi dan Dokumentasi
5. **Buat Adjustment yang Diperlukan**
   - Aksi: Koreksi inkonsistensi teridentifikasi:
     - Siswa A: Maintain 12/12 (excellence terjustifikasi)
     - Siswa B: Adjust 2/12 → 5/12 (koreksi error grading)
     - Siswa C: Adjust kasus perbandingan untuk konsistensi
   - Verifikasi:
     - Perubahan tracked dan documented
     - Siswa dinotifikasi adjustment
     - Reasoning diberikan untuk semua changes
     - Metrik kualitas updated
     - Learning proses captured

### Kriteria Sukses
- [ ] Inkonsistensi identified accurately
- [ ] Proses review thorough dan fair
- [ ] Koreksi dibuat appropriately
- [ ] Dokumentasi komprehensif
- [ ] Kualitas improved untuk grading future

---

## PP-AP-003: Pengajar - Isu Teknis Saat Grading

### Informasi Skenario
- **ID Skenario**: PP-AP-003
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: Isu Teknis Sistem

### Prasyarat
- Sesi grading sedang progress
- Sistem mengalami kesulitan teknis
- Work-in-progress perlu preservation
- Workflow alternatif diperlukan

### Data Test
```
Skenario Kegagalan Teknis:
Progress: 15/25 essay dinilai
Waktu Invested: 2 jam
Isu: Sistem crash saat save
Auto-save Terakhir: 10 menit lalu
Risk: Loss feedback comments detail

Problem Spesifik:
- Browser freeze saat entry feedback
- Network timeout saat submit nilai
- Audio playback failure untuk bacaan
- File corruption dalam display essay
```

### Langkah Pengujian

#### Bagian 1: Handle System Crash
1. **Alami Kegagalan Sistem**
   - Aksi: Sistem crash saat entering feedback detail
   - Verifikasi:
     - Recovery auto-save tersedia
     - Work preserved hingga save point terakhir
     - Instruksi recovery jelas
     - Metode akses alternatif provided
     - Info kontak support terlihat

#### Bagian 2: Proses Recovery
2. **Recover dan Lanjutkan Grading**
   - Aksi: Gunakan mekanisme recovery
   - Verifikasi:
     - Work sebelumnya restored accurately
     - Dapat identify yang hilang
     - Metode grading alternatif tersedia
     - Opsi backup offline working
     - Incident ter-log otomatis

3. **Handle Isu Playback Audio**
   - Aksi: File audio tidak bisa play untuk grading bacaan
   - Verifikasi:
     - Audio player alternatif tersedia
     - Opsi download untuk player eksternal
     - Technical support accessible
     - Dapat defer grading hingga fixed
     - Notifikasi siswa possible

#### Bagian 3: Workflow Alternatif
4. **Gunakan Mode Grading Offline**
   - Aksi: Switch ke offline grading saat sistem unavailable
   - Verifikasi:
     - Mode offline fungsional
     - Dapat export submission siswa
     - Template grading spreadsheet tersedia
     - Proses re-import smooth
     - Integritas data maintained

### Kriteria Sukses
- [ ] Isu teknis handled gracefully
- [ ] Preservasi work efektif
- [ ] Workflow alternatif tersedia
- [ ] Proses recovery reliable
- [ ] Minimal disruption ke jadwal grading

---

## PP-AP-004: Pengajar - Konflik Grading Kolaboratif

### Informasi Skenario
- **ID Skenario**: PP-AP-004
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR (Multiple)
- **Estimasi Waktu**: 15-18 menit
- **Tipe Error**: Isu Koordinasi Workflow

### Prasyarat
- Multiple instructor assigned untuk grade ujian sama
- Section berbeda require konsistensi
- Disagreement dalam approach scoring
- Mekanisme koordinasi diperlukan

### Data Test
```
Skenario Grading Kolaboratif:
Ujian: Final Examination - Tajwid Lanjutan
Penilai: 3 instructor
- Ustadz Ahmad: Essay Q1-Q3
- Ustadz Ibrahim: Essay Q4-Q6
- Ustadz Mahmud: Bacaan Q7-Q10

Isu: Inkonsistensi scoring antar grader
Ahmad avg: 85% (lenient)
Ibrahim avg: 72% (strict)
Mahmud avg: 78% (moderate)

Keluhan Siswa: Unfair scoring antar section
```

### Langkah Pengujian

#### Bagian 1: Identifikasi Disparitas Scoring
1. **Deteksi Inkonsistensi Grading**
   - Aksi: Sistem flag disparitas scoring
   - Verifikasi:
     - Statistik cross-grader terlihat
     - Alert disparitas generated
     - Assignment section siswa jelas
     - Metrik fairness calculated
     - Meeting koordinasi triggered

#### Bagian 2: Meeting Koordinasi
2. **Lakukan Sesi Kalibrasi Grader**
   - Aksi: Meet untuk standardize approach scoring
   - Verifikasi:
     - Sample jawaban reviewed together
     - Interpretasi rubrik clarified
     - Contoh scoring established
     - Konsensus standar reached
     - Protokol adjustment agreed

#### Bagian 3: Proses Re-kalibrasi
3. **Apply Standar Konsisten**
   - Aksi: Re-evaluate sample graded work
   - Verifikasi:
     - Seleksi sample random
     - Proses re-grading blind
     - Kalkulasi adjustment skor
     - Proses notifikasi siswa
     - Resolusi fair achieved

### Kriteria Sukses
- [ ] Inkonsistensi identified quickly
- [ ] Proses koordinasi efektif
- [ ] Standar harmonized
- [ ] Adjustment fair dibuat
- [ ] Kepercayaan siswa maintained

---

## PP-AP-005: Pengajar - Dilema Grading Assignment Terlambat

### Informasi Skenario
- **ID Skenario**: PP-AP-005
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Isu Aplikasi Kebijakan

### Prasyarat
- Siswa submit ujian setelah deadline
- Medical excuse provided
- Interpretasi kebijakan diperlukan
- Pertimbangan fair treatment

### Data Test
```
Skenario Submission Terlambat:
Siswa: Omar Hassan
Submission: 3 hari setelah deadline
Alasan: Emergency keluarga (hospitalization)
Dokumentasi: Surat keterangan medis provided
Kebijakan: "Penalty keterlambatan dapat waived untuk emergency medis"

Pertimbangan Grading:
- Penalty standar: -10% per hari = -30%
- Medical waiver possible
- Fairness ke siswa lain
- Compliance kebijakan akademik
```

### Langkah Pengujian

#### Bagian 1: Review Submission Terlambat
1. **Assess Request Submission Terlambat**
   - Aksi: Review dokumentasi medis siswa
   - Verifikasi:
     - Autentisitas dokumentasi verified
     - Guideline kebijakan accessible
     - Kasus preseden tersedia
     - Framework keputusan jelas
     - Konsultasi administratif available

#### Bagian 2: Apply Fair Judgment
2. **Buat Keputusan Kebijakan**
   - Aksi: Tentukan aplikasi penalty
   - Verifikasi:
     - Medical excuse validated
     - Interpretasi kebijakan documented
     - Rationale fair treatment recorded
     - Notifikasi siswa sent
     - Audit trail maintained

### Kriteria Sukses
- [ ] Kebijakan applied fairly
- [ ] Dokumentasi adequate
- [ ] Rationale keputusan jelas
- [ ] Siswa treated dengan compassion
- [ ] Standar institusional maintained

---

## PP-AP-006: Pengajar - Edge Case Batas Nilai

### Informasi Skenario
- **ID Skenario**: PP-AP-006
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: Keputusan Batas Assessment

### Prasyarat
- Skor siswa sangat dekat dengan grade boundaries
- Implikasi lulus/gagal significant
- Pertimbangan assessment holistik diperlukan
- Academic progression at stake

### Data Test
```
Kasus Batas Nilai:
Nilai Kelulusan: 70/100

Siswa Borderline:
1. Aisha: 69.5/100 (dibulatkan ke 70?)
2. Yusuf: 69.2/100 (performa borderline konsisten)
3. Zaynab: 69.8/100 (trend improvement kuat)

Pertimbangan:
- Kebijakan pembulatan
- Progress siswa overall
- Effort dan partisipasi
- Likelihood sukses akademik future
```

### Langkah Pengujian

#### Bagian 1: Review Kasus Borderline
1. **Analisis Performa Borderline**
   - Aksi: Review profil lengkap setiap siswa
   - Verifikasi:
     - Histori nilai accessible
     - Record partisipasi terlihat
     - Trend improvement shown
     - Keadaan khusus noted
     - Panduan kebijakan available

#### Bagian 2: Apply Assessment Holistik
2. **Buat Keputusan Informed**
   - Aksi: Pertimbangkan semua faktor untuk setiap siswa
   - Verifikasi:
     - Multiple data point considered
     - Aplikasi fair dan konsisten
     - Educational benefit prioritized
     - Dokumentasi komprehensif
     - Student development focused

### Kriteria Sukses
- [ ] Assessment holistik komprehensif
- [ ] Keputusan educationally sound
- [ ] Konsistensi maintained
- [ ] Student welfare prioritized
- [ ] Standar akademik preserved

---

## PP-AP-007: Pengajar - Dugaan Ketidakjujuran Akademik

### Informasi Skenario
- **ID Skenario**: PP-AP-007
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 12-15 menit
- **Tipe Error**: Isu Integritas Akademik

### Prasyarat
- Similaritas mencurigakan antar jawaban siswa
- Indikator potential cheating
- Enforcement kebijakan integritas akademik
- Prosedur investigasi diperlukan

### Data Test
```
Dugaan Kecurangan:
Siswa: Khalid dan Saeed
Isu: Jawaban essay hampir identik
Similaritas: 95% content match
Bukti:
- Contoh unusual sama digunakan
- Struktur phrase identik
- Terminologi rare sama
- Waktu submission berurutan

Investigasi Diperlukan:
- Compare jawaban detail
- Review background siswa
- Apply kebijakan integritas akademik
- Proses hearing fair
```

### Langkah Pengujian

#### Bagian 1: Dokumentasi Bukti
1. **Kumpulkan Bukti Kecurangan**
   - Aksi: Dokumentasikan similaritas mencurigakan
   - Verifikasi:
     - Perbandingan side-by-side tersedia
     - Metrik similaritas calculated
     - Analisis timestamp shown
     - Konteks tambahan gathered
     - Prosedur preservasi bukti

#### Bagian 2: Proses Integritas Akademik
2. **Follow Prosedur Investigasi**
   - Aksi: Inisiasi investigasi integritas akademik
   - Verifikasi:
     - Prosedur proper followed
     - Siswa given fair hearing
     - Bukti evaluated objektif
     - Konsekuensi appropriate applied
     - Peluang edukatif provided

### Kriteria Sukses
- [ ] Bukti gathered thoroughly
- [ ] Proses followed correctly
- [ ] Siswa treated fairly
- [ ] Integritas akademik maintained
- [ ] Nilai edukatif preserved

---

## PP-AP-008: Pengajar - Error Kalkulasi Sistem Grading

### Informasi Skenario
- **ID Skenario**: PP-AP-008
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Error Kalkulasi Sistem

### Prasyarat
- Kalkulasi nilai otomatis appear incorrect
- Verifikasi manual reveal discrepancies
- Nilai siswa affected
- Prosedur koreksi diperlukan

### Data Test
```
Error Kalkulasi:
Isu: Kalkulasi nilai berbobot incorrect
Affected: 15 siswa
Error: Bobot bacaan applied incorrectly (15% instead of 10%)
Impact: Nilai final inflated 3-5 poin

Discovery: Manual spot check reveal discrepancy
Verifikasi: Multiple kasus confirmed
Required Action: Koreksi sistematis
```

### Langkah Pengujian

#### Bagian 1: Identifikasi Error Sistem
1. **Deteksi Problem Kalkulasi**
   - Aksi: Notice discrepancy saat verifikasi manual
   - Verifikasi:
     - Pattern error identifiable
     - Scope impact assessable
     - Root cause determinable
     - Metode koreksi available
     - Impact siswa calculable

#### Bagian 2: Proses Koreksi
2. **Implement Fix Sistematis**
   - Aksi: Koreksi semua nilai affected
   - Verifikasi:
     - Rekalkulasi akurat performed
     - Semua siswa affected identified
     - Koreksi applied konsisten
     - Siswa notified appropriately
     - Audit trail maintained

### Kriteria Sukses
- [ ] Error identified quickly
- [ ] Proses koreksi thorough
- [ ] Siswa treated fairly
- [ ] Integritas sistem restored
- [ ] Measure pencegahan implemented

---

## Matrix Recovery Error untuk Grading Pengajar

| Tipe Error | Severity | Waktu Recovery | Impact | Proses Resolusi |
|------------|----------|----------------|--------|-----------------|
| Banding Nilai | Medium | 3-5 hari | Individual | Review terstruktur |
| Inkonsistensi | High | 1-2 hari | Multiple siswa | Analisis statistik |
| Isu Teknis | Medium | <4 jam | Delay workflow | Auto-recovery |
| Konflik Grader | High | 1 minggu | Fairness section | Meeting kalibrasi |
| Submission Terlambat | Low | Same day | Individual | Aplikasi kebijakan |
| Batas Nilai | Medium | 2-3 hari | Individual | Review holistik |
| Ketidakjujuran Akademik | High | 1-2 minggu | Integritas akademik | Proses investigasi |
| Error Sistem | High | <24 jam | Multiple siswa | Koreksi sistematis |

## Protokol Quality Assurance

### Akurasi Grading
- [ ] Deteksi outlier statistik automated
- [ ] Monitoring konsistensi cross-grader
- [ ] Random quality check implemented
- [ ] Integrasi feedback siswa
- [ ] Tracking continuous improvement

### Fair Treatment
- [ ] Proses banding accessible
- [ ] Mekanisme deteksi bias
- [ ] Aplikasi standar konsisten
- [ ] Requirement dokumentasi
- [ ] Support student advocacy

### Reliability Sistem
- [ ] Frekuensi auto-save adequate
- [ ] Prosedur backup robust
- [ ] Mekanisme recovery tested
- [ ] Workflow alternatif available
- [ ] Technical support responsive

---

**Catatan Testing**:
- Error grading pengajar dapat impact outcome siswa significantly
- Proses fair dan transparan esensial untuk integritas akademik
- Reliability teknis kritis untuk kepercayaan instructor
- Quality assurance prevent unfairness sistemik
- Student appeals harus handled dengan dignity dan thoroughness