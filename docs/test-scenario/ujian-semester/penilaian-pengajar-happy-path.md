# Skenario Pengujian: Penilaian Ujian Pengajar - Happy Path

## Informasi Umum
- **Kategori**: Ujian Semester - Penilaian dan Evaluasi Pengajar
- **Modul**: Alur Penilaian dan Assessment Pengajar
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama untuk proses penilaian pengajar
- **Playwright Test**: `teacher-grading.TeacherGradingTest`

---

## PP-HP-001: Pengajar - Penilaian Manual Essay dengan Rubrik

### Informasi Skenario
- **ID Skenario**: PP-HP-001 (Penilaian Pengajar - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 20-25 menit
- **Playwright Test**: `TeacherGradingTest.manualEssayGrading()`

### Prasyarat
- Akun pengajar: `ustadz.ahmad` / `Welcome@YSQ2024`
- Ujian tengah semester telah diselesaikan siswa
- Soal essay memerlukan penilaian manual
- Rubrik sudah didefinisikan untuk setiap essay
- 15 siswa telah submit ujian

### Data Test
```
Sesi Penilaian Essay:
Ujian: Ujian Tengah Semester - Tahsin 2
Soal Essay: 5 soal (Q31-Q35)
Siswa untuk Dinilai: 15 submission
Rubrik: Skala 3 poin per kriteria

Contoh Essay (Q31):
Pertanyaan: "Jelaskan perbedaan Idgham Bighunnah dan Bilaghunnah dengan contoh"
Kriteria Rubrik:
- Ketepatan Definisi (0-3 poin)
- Contoh yang Diberikan (0-3 poin)
- Ketepatan Teks Arab (0-3 poin)
- Kejelasan Penjelasan (0-3 poin)
Total: 12 poin per essay
```

### Langkah Pengujian

#### Bagian 1: Akses Dashboard Penilaian
1. **Login dan Navigasi ke Penilaian**
   - Aksi: Login sebagai pengajar
   - Verifikasi:
     - Dashboard pengajar tampil
     - Bagian "Menunggu Penilaian" terlihat
     - Daftar ujian menunjukkan "15 submission menunggu penilaian"
     - Prioritas penilaian essay terhighlight

2. **Pilih Ujian untuk Dinilai**
   - Aksi: Klik "Nilai Essay" pada ujian Midterm
   - Verifikasi:
     - Interface penilaian ter-load
     - Daftar siswa menunjukkan 15 submission
     - Navigasi soal tersedia
     - Panel rubrik terlihat
     - Indikator progress: "0/75 essay dinilai"

#### Bagian 2: Proses Penilaian Essay
3. **Review Soal Essay dan Rubrik**
   - Aksi: Pilih Q31 untuk siswa pertama (Ali Rahman)
   - Verifikasi:
     - Soal asli ditampilkan jelas
     - Jawaban siswa dalam format mudah dibaca
     - Kriteria rubrik dengan nilai poin
     - Jawaban sebelumnya dapat diakses untuk konsistensi
     - Referensi jawaban contoh tersedia

4. **Terapkan Penilaian Rubrik**
   - Aksi: Nilai essay Q31 Ali:
     - Ketepatan Definisi: 3/3 (pemahaman sangat baik)
     - Contoh Diberikan: 2/3 (contoh bagus, kurang satu)
     - Ketepatan Arab: 3/3 (arab sempurna)
     - Kejelasan: 2/3 (jelas tapi bisa lebih terorganisir)
   - Verifikasi:
     - Pemilihan poin lancar (klik atau dropdown)
     - Total berjalan dihitung otomatis (10/12)
     - Dapat menambah feedback tertulis
     - Progress auto-save

5. **Berikan Feedback Tertulis**
   - Aksi: Tambah feedback konstruktif:
     ```
     "Jawaban sangat baik! Definisi Idgham sudah tepat dan contoh
     Arab juga akurat. Untuk peningkatan: tambahkan contoh Bilaghunnah
     dengan huruf 'Lam' dan perbaiki struktur penjelasan agar lebih
     sistematis. Overall: pemahaman konsep excellent."
     ```
   - Verifikasi:
     - Editor teks dengan opsi format
     - Jumlah karakter terlihat
     - Dapat insert teks Arab
     - Auto-save feedback
     - Template tersedia untuk feedback umum

#### Bagian 3: Efisiensi Penilaian Batch
6. **Nilai Multiple Siswa dengan Efisien**
   - Aksi: Lanjutkan menilai Q31 untuk 14 siswa lainnya
   - Verifikasi:
     - Navigasi cepat antar siswa
     - Skor rubrik sebelumnya terlihat untuk konsistensi
     - Dapat mark jawaban serupa dengan cepat
     - Opsi komentar bulk tersedia
     - Tracker progress update (15/75 selesai)

7. **Review Konsistensi Penilaian**
   - Aksi: Gunakan tool cek konsistensi
   - Verifikasi:
     - Overview statistik skor yang diberikan
     - Deteksi outlier (skor terlalu tinggi/rendah)
     - Chart distribusi terlihat
     - Dapat review nilai yang questionable
     - Catatan untuk justifikasi

#### Bagian 4: Selesaikan Semua Essay
8. **Nilai Soal Essay Tersisa**
   - Aksi: Selesaikan Q32-Q35 untuk semua siswa
   - Verifikasi:
     - Workflow efisien terjaga
     - Konsistensi lintas soal
     - Total skor essay terhitung
     - Dapat flag jawaban exceptional
     - Progress: 75/75 essay selesai

9. **Review Final dan Submit**
   - Aksi: Review semua nilai essay sebelum submit
   - Verifikasi:
     - Ringkasan nilai per siswa
     - Ringkasan nilai per soal
     - Dapat adjust skor individual
     - Preview feedback tersedia
     - Submit nilai dengan konfirmasi

### Hasil Diharapkan
- Semua 75 essay dinilai dengan scoring rubrik detail
- Aplikasi standar penilaian konsisten
- Feedback konstruktif diberikan untuk setiap siswa
- Skor essay terintegrasi dengan total ujian
- Siswa dapat melihat feedback detail

### Kriteria Sukses
- [ ] Aplikasi rubrik konsisten dan adil
- [ ] Interface penilaian efisien untuk bulk work
- [ ] Kualitas feedback membantu improvement siswa
- [ ] Konsistensi statistik terjaga
- [ ] Integrasi dengan skor ujian keseluruhan seamless

---

## PP-HP-002: Pengajar - Penilaian Praktik Bacaan Al-Quran

### Informasi Skenario
- **ID Skenario**: PP-HP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 25-30 menit

### Prasyarat
- Recording bacaan Quran sudah disubmit siswa
- 5 tugas bacaan per siswa (Al-Fatihah, An-Nas, dll)
- Kriteria assessment untuk evaluasi tajwid
- Tool playback audio terkonfigurasi

### Data Test
```
Assessment Bacaan:
Siswa: 15 recording masing-masing
Tugas: 5 bacaan Quran per siswa
Total Recording: 75 file audio
Kriteria Assessment:
- Makharijul Huruf (Pelafalan): 0-4 poin
- Ahkam Tajwid (Aturan): 0-4 poin
- Kelancaran & Irama: 0-2 poin
Total: 10 poin per bacaan
```

### Langkah Pengujian

#### Bagian 1: Setup Assessment Audio
1. **Akses Interface Penilaian Bacaan**
   - Aksi: Navigasi ke penilaian ujian praktik
   - Verifikasi:
     - Audio player ter-embed
     - Daftar siswa dengan status recording
     - Kriteria assessment terlihat
     - Kontrol playback berfungsi
     - Teks ayat ditampilkan untuk referensi

2. **Test Playback Audio**
   - Aksi: Play recording pertama (Ali - Al-Fatihah)
   - Verifikasi:
     - Kualitas audio jelas
     - Kontrol play/pause/seek bekerja
     - Adjustment kecepatan tersedia (0.5x, 1x, 1.5x)
     - Kontrol volume berfungsi
     - Dapat replay bagian spesifik

#### Bagian 2: Assessment Bacaan Detail
3. **Nilai Makharijul Huruf**
   - Aksi: Evaluasi ketepatan pelafalan
   - Dengar untuk error spesifik:
     - Kejelasan Huruf Qalqalah
     - Perbedaan Makhraj (Ta vs Tha)
     - Panjang yang tepat (Mad)
   - Verifikasi:
     - Dapat mark timestamp error spesifik
     - Kategori error tersedia
     - Rubrik scoring jelas (skala 0-4)
     - Dapat add catatan/komentar audio

4. **Evaluasi Aturan Tajwid**
   - Aksi: Cek penerapan aturan tajwid:
     - Aplikasi Idgham
     - Implementasi Iqlab
     - Panjang Ghunnah
     - Aturan Waqf (berhenti)
   - Verifikasi:
     - Checkpoint spesifik per aturan
     - Dapat mark pelanggaran aturan
     - Panduan referensi accessible
     - Scoring akurat per aturan

5. **Nilai Kelancaran & Irama**
   - Aksi: Evaluasi flow bacaan keseluruhan
   - Verifikasi:
     - Irama natural terjaga
     - Jeda yang tepat
     - Transisi halus
     - Kesesuaian emosional
     - Scoring kesan keseluruhan

#### Bagian 3: Feedback dan Scoring
6. **Berikan Feedback Spesifik**
   - Aksi: Catat feedback detail untuk Al-Fatihah Ali:
     ```
     Pelafalan: Sangat Baik (4/4)
     - Semua poin makhraj akurat
     - Qalqalah jelas pada "qad"

     Aturan Tajwid: Baik (3/4)
     - Idgham benar di "min rabbil"
     - Mad Natural sempurna
     - Minor: Ghunnah bisa lebih panjang di "min"

     Kelancaran: Sangat Baik (2/2)
     - Irama natural terjaga
     - Penghayatan tepat

     Total: 9/10
     Overall: Masyaa Allah, bacaan sangat bagus!
     ```
   - Verifikasi:
     - Breakdown detail per kriteria
     - Saran improvement spesifik
     - Encouragement disertakan
     - Kalkulasi skor otomatis

7. **Workflow Assessment Efisien**
   - Aksi: Nilai recording sisanya dengan workflow efisien
   - Verifikasi:
     - Navigasi cepat antar recording
     - Template feedback umum tersedia
     - Operasi bulk untuk skor serupa
     - Tracking progress terlihat
     - Dapat flag bacaan exceptional

### Kriteria Sukses
- [ ] Tool assessment audio efektif
- [ ] Evaluasi tajwid komprehensif
- [ ] Feedback spesifik dan helpful
- [ ] Scoring konsisten antar siswa
- [ ] Workflow efisien untuk volume besar

---

## PP-HP-003: Pengajar - Penilaian Batch dengan Quick Feedback

### Informasi Skenario
- **ID Skenario**: PP-HP-003
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-18 menit

### Prasyarat
- Soal pilihan ganda sudah auto-graded
- Soal jawaban singkat perlu quick review
- 30 siswa selesai quiz
- Time pressure untuk release nilai

### Data Test
```
Sesi Quick Grading:
Assessment: Quiz Mingguan Bab 4
Soal MCQ: 8 (auto-graded)
Jawaban Singkat: 2 soal
Siswa: 30 submission
Target: Selesai grading dalam 15 menit
```

### Langkah Pengujian

#### Bagian 1: Review Auto-Graded Sections
1. **Verifikasi Akurasi Auto-Grading**
   - Aksi: Review hasil auto-grading MCQ
   - Verifikasi:
     - Akurasi auto-grade terverifikasi
     - Mekanisme dispute tersedia
     - Dapat manual override jika perlu
     - Ringkasan statistik ditampilkan

#### Bagian 2: Quick Grading Jawaban Singkat
2. **Gunakan Template Quick Grading**
   - Aksi: Nilai jawaban singkat menggunakan template
   - Verifikasi:
     - Pattern jawaban umum dikenali
     - Pre-written feedback tersedia
     - Bulk apply untuk nilai serupa
     - Custom feedback masih possible

3. **Proses Review Efisien**
   - Aksi: Selesaikan semua grading dalam time limit
   - Verifikasi:
     - Timer visible untuk tracking
     - Keyboard shortcut cepat
     - Distribusi feedback massal
     - Kalkulasi nilai immediate

### Kriteria Sukses
- [ ] Grading selesai dalam target waktu
- [ ] Kualitas terjaga meski cepat
- [ ] Feedback siswa adequate
- [ ] Proses streamlined dan efisien

---

## PP-HP-004: Pengajar - Kalkulasi Nilai & Scoring Final

### Informasi Skenario
- **ID Skenario**: PP-HP-004
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Semua komponen individual sudah dinilai
- Skema pembobotan sudah didefinisikan
- Kebijakan poin bonus applicable
- Batas nilai final sudah ditetapkan

### Data Test
```
Kalkulasi Nilai:
Komponen:
- MCQ (30 soal): 60 poin (60%)
- Essay (5 soal): 30 poin (30%)
- Bacaan (5 tugas): 10 poin (10%)
- Bonus: Hingga 5 poin ekstra
- Nilai Kelulusan: 70/100

Contoh Kalkulasi:
Ali Rahman:
- MCQ: 54/60 (90%)
- Essay: 26/30 (87%)
- Bacaan: 9/10 (90%)
- Bonus: +2 (partisipasi baik)
- Total: 91/100 (A-)
```

### Langkah Pengujian

#### Bagian 1: Integrasi Komponen
1. **Verifikasi Skor Komponen**
   - Aksi: Review semua komponen yang dinilai
   - Verifikasi:
     - Semua bagian selesai
     - Skor tercatat dengan benar
     - Tidak ada nilai hilang
     - Adjustment manual dicatat

#### Bagian 2: Apply Pembobotan & Kalkulasi
2. **Hitung Skor Berbobot**
   - Aksi: Terapkan formula pembobotan
   - Verifikasi:
     - Kalkulasi otomatis akurat
     - Manual override tersedia
     - Aturan pembulatan tepat
     - Poin bonus ditambah sesuai

3. **Assign Nilai Huruf**
   - Aksi: Terapkan grade boundaries
   - Verifikasi:
     - Skala nilai diterapkan benar
     - Kasus border handled fair
     - Dapat adjust kasus individual
     - Distribusi nilai reasonable

#### Bagian 3: Review Final
4. **Review dan Finalisasi Nilai**
   - Aksi: Review nilai final sebelum release
   - Verifikasi:
     - Ringkasan nilai akurat
     - Dapat identify outlier
     - Distribusi statistik shown
     - Siap untuk publikasi

### Kriteria Sukses
- [ ] Kalkulasi nilai akurat
- [ ] Pembobotan applied correctly
- [ ] Distribusi nilai fair achieved
- [ ] Proses transparan dan auditable

---

## PP-HP-005: Pengajar - Review Analytics Performa Siswa

### Informasi Skenario
- **ID Skenario**: PP-HP-005
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Semua nilai sudah final
- Sistem analytics terkonfigurasi
- Data performa kelas tersedia
- Data perbandingan accessible

### Data Test
```
Analytics Performa:
Kelas: Tahsin 2 - Senin Pagi
Siswa: 15
Ujian: Midterm Semester 1

Metrik Kunci:
- Rata-rata Kelas: 78.5
- Nilai Tertinggi: 94 (Fatima)
- Nilai Terendah: 62 (Omar)
- Standar Deviasi: 8.7
- Tingkat Kelulusan: 87% (13/15)
```

### Langkah Pengujian

#### Bagian 1: Overview Performa Kelas
1. **Review Statistik Kelas**
   - Aksi: Akses dashboard performa
   - Verifikasi:
     - Metrik kunci displayed clearly
     - Chart visual tersedia
     - Perbandingan dengan kelas lain
     - Data trend historis

#### Bagian 2: Analisis Siswa Individual
2. **Identifikasi Siswa Perlu Support**
   - Aksi: Review siswa dengan performa rendah
   - Verifikasi:
     - Breakdown performa individual
     - Area kelemahan teridentifikasi
     - Rekomendasi improvement
     - Saran intervensi

3. **Kenali High Performer**
   - Aksi: Acknowledge performa excellent
   - Verifikasi:
     - Identifikasi top performer
     - Analisis area kekuatan
     - Peluang mentoring potensial
     - Saran tantangan lanjutan

#### Bagian 3: Review Efektivitas Pengajaran
4. **Analisis Performa Soal**
   - Aksi: Review soal mana yang challenging
   - Verifikasi:
     - Analisis tingkat kesulitan soal
     - Pattern error umum
     - Level penguasaan topik
     - Rekomendasi adjustment pengajaran

### Kriteria Sukses
- [ ] Analytics komprehensif dan actionable
- [ ] Insight siswa individual valuable
- [ ] Data improvement pengajaran tersedia
- [ ] Trend performa identifiable

---

## PP-HP-006: Pengajar - Banding Nilai & Komunikasi

### Informasi Skenario
- **ID Skenario**: PP-HP-006
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Nilai sudah dipublish ke siswa
- Proses banding nilai defined
- Pertanyaan siswa diterima
- Protokol review tersedia

### Data Test
```
Skenario Banding Nilai:
Siswa: Omar Hassan
Nilai Asli: 68/100 (Gagal)
Fokus Banding: Scoring essay Q33
Klaim: "Yakin jawaban deserve skor lebih tinggi"
Diperlukan: Proses re-evaluasi

Detail Essay Q33:
Skor Asli: 6/12
Klaim Siswa: Seharusnya 9/12
Area Rubrik Diperdebatkan:
- Ketepatan Definisi
- Contoh yang Diberikan
```

### Langkah Pengujian

#### Bagian 1: Terima Request Banding
1. **Proses Banding Nilai**
   - Aksi: Terima banding melalui sistem
   - Verifikasi:
     - Notifikasi banding diterima
     - Alasan siswa terlihat
     - Jawaban asli accessible
     - Histori grading tersedia

#### Bagian 2: Proses Re-evaluasi
2. **Lakukan Re-evaluasi Fair**
   - Aksi: Re-review essay yang disputed
   - Verifikasi:
     - Rubrik asli diterapkan
     - Assessment bebas bias
     - Opsi second opinion
     - Dokumentasi diperlukan

3. **Berikan Respon Detail**
   - Aksi: Komunikasikan keputusan dengan reasoning jelas
   - Verifikasi:
     - Penjelasan detail diberikan
     - Referensi ke kriteria spesifik
     - Nilai edukatif dijaga
     - Komunikasi respectful

#### Bagian 3: Dokumentasi Resolusi
4. **Dokumentasi Resolusi Banding**
   - Aksi: Catat outcome banding
   - Verifikasi:
     - Keputusan dicatat
     - Reasoning didokumentasi
     - Siswa dinotifikasi
     - Nilai diupdate jika perlu

### Kriteria Sukses
- [ ] Proses banding fair dan transparan
- [ ] Re-evaluasi thorough
- [ ] Komunikasi professional
- [ ] Dokumentasi complete

---

## Integrasi dengan Workflow Pengajaran

### Metrik Efisiensi Penilaian

| Proses | Target Waktu | Akurasi | Benefit Siswa |
|--------|--------------|---------|---------------|
| Penilaian Essay | 3 menit/essay | 95% compliance rubrik | Feedback detail |
| Assessment Bacaan | 4 menit/recording | 90% akurasi tajwid | Improvement spesifik |
| Quick Grading | 30 detik/siswa | 98% auto-verifikasi | Feedback cepat |
| Kalkulasi Nilai | 1 menit/kelas | 100% akurasi | Nilai final fair |
| Review Analytics | 10 menit/kelas | Insight komprehensif | Support tertarget |

## Standar Quality Assurance

### Konsistensi Penilaian
- [ ] Kepatuhan rubrik dimonitor
- [ ] Deteksi outlier statistik
- [ ] Peer review untuk kasus kompleks
- [ ] Incorporasi feedback siswa
- [ ] Proses continuous improvement

### Komunikasi Siswa
- [ ] Release nilai tepat waktu (dalam 7 hari)
- [ ] Feedback detail diberikan
- [ ] Panduan improvement jelas
- [ ] Proses banding fair
- [ ] Interaksi respectful dijaga

### Pengembangan Profesional
- [ ] Training best practice penilaian
- [ ] Profisiensi tool teknologi
- [ ] Workshop desain rubrik
- [ ] Pengembangan literasi assessment
- [ ] Kolaborasi peer didorong

---

**Catatan Implementasi**:
- Workflow penilaian pengajar kritis untuk kualitas akademik
- Tool konsistensi esensial untuk keadilan
- Kualitas feedback siswa impact hasil pembelajaran
- Analytics mendorong improvement pengajaran
- Teknologi harus enhance, bukan replace, professional judgment