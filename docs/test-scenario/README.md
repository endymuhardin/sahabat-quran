# Dokumentasi Skenario Pengujian Manual
# Aplikasi Manajemen Yayasan Sahabat Quran

## Gambaran Umum

Folder ini berisi dokumentasi skenario pengujian manual yang dirancang untuk human tester dalam melakukan validasi fungsional aplikasi. Setiap skenario pengujian manual memiliki referensi ke automated test (Playwright) yang sesuai untuk memastikan konsistensi coverage.

## Struktur Dokumen

### 📂 Organisasi File

```
test-scenario/
├── README.md                          # Panduan umum skenario pengujian
├── pendaftaran-siswa-happy-path.md    # Skenario sukses pendaftaran siswa
├── pendaftaran-siswa-alternate-path.md # Skenario error & validasi pendaftaran
├── admin-registrasi-happy-path.md     # Skenario sukses manajemen admin
├── admin-registrasi-alternate-path.md # Skenario error & validasi admin
├── tes-penempatan-happy-path.md       # Skenario sukses evaluasi tes penempatan
└── tes-penempatan-alternate-path.md   # Skenario error & validasi tes penempatan
```

### 🎯 Kategorisasi Skenario

#### Happy Path (Jalur Sukses)
- **Tujuan**: Memvalidasi alur bisnis utama berjalan dengan sempurna
- **Kondisi**: Data valid, akses sesuai role, tidak ada error
- **Hasil Diharapkan**: Semua proses selesai dengan sukses

#### Alternate Path (Jalur Alternatif)
- **Tujuan**: Memvalidasi penanganan error dan validasi sistem
- **Kondisi**: Data invalid, akses tidak sesuai, kondisi edge case
- **Hasil Diharapkan**: Sistem menampilkan pesan error yang sesuai

## Pemetaan dengan Automated Test

Setiap skenario manual memiliki referensi ke automated test Playwright:

| Skenario Manual | Playwright Test | Lokasi File |
|-----------------|---------------|-------------|
| Pendaftaran Siswa - Happy Path | `StudentRegistrationPlaywrightTest` | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |
| Pendaftaran Siswa - Alternate Path | `StudentRegistrationPlaywrightTest` (validation scenarios) | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |
| Admin Registrasi - Happy Path | `LoginAndNavigationPlaywrightTest` (admin scenarios) | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |
| Admin Registrasi - Alternate Path | `LoginAndNavigationPlaywrightTest` (validation scenarios) | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |
| Tes Penempatan - Happy Path | Future Playwright test | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |
| Tes Penempatan - Alternate Path | Future Playwright test | `src/test/java/com/sahabatquran/webapp/functional/playwright/` |

## Format Standar Skenario

### Template Skenario
```markdown
## Skenario: [Nama Skenario]

### Informasi Umum
- **ID Skenario**: [ID unik]
- **Modul**: [Nama modul]
- **Prioritas**: [Tinggi/Sedang/Rendah]
- **Tipe**: [Happy Path/Alternate Path]
- **Playwright Test**: [Nama class dan method]

### Prasyarat
- [Kondisi yang harus dipenuhi sebelum test]

### Data Test
- [Data yang dibutuhkan untuk test]

### Langkah Pengujian
1. [Langkah detail dengan aksi dan verifikasi]
2. [Langkah berikutnya]

### Hasil Diharapkan
- [Hasil yang diharapkan muncul]

### Kriteria Sukses
- [Kriteria untuk menentukan test berhasil]
```

## Petunjuk Eksekusi untuk Tester

### Persiapan Environment
1. **Akses Aplikasi**: `http://localhost:8080`
2. **Akun Test**:
   - Admin: `admin` / `AdminYSQ@2024`
   - User: `user` / `UserYSQ@2024`
3. **Database**: Akan di-reset setiap test dengan data sample

### Eksekusi Manual Test
1. **Baca skenario lengkap** terlebih dahulu
2. **Persiapkan data test** sesuai dokumentasi
3. **Ikuti langkah pengujian** secara berurutan
4. **Catat setiap penyimpangan** dari hasil diharapkan
5. **Dokumentasikan bug** dengan screenshot dan deskripsi detail

### Pelaporan Hasil Test
- **Format**: Bug report dengan ID skenario sebagai referensi
- **Screenshot**: Wajib untuk setiap bug/error yang ditemukan
- **Severity**: Critical/High/Medium/Low
- **Environment**: Browser, OS, timestamp eksekusi

## Integrasi dengan Automated Test

### Validasi Konsistensi
1. **Manual test** dijalankan untuk validasi user experience
2. **Automated test** dijalankan untuk validasi technical implementation
3. **Hasil keduanya** dibandingkan untuk memastikan konsistensi

### Command Automated Test
```bash
# Jalankan semua automated test
./mvnw test

# Jalankan test per business process
./mvnw test -Dtest="*StudentRegistration*"
./mvnw test -Dtest="*AdminRegistration*"
./mvnw test -Dtest="*PlacementTest*"

# Jalankan test per tipe
./mvnw test -Dtest="*HappyPath*"
./mvnw test -Dtest="*Validation*"
```

## Best Practices untuk Tester

### Persiapan Test
- Selalu mulai dengan fresh browser session
- Clear cache dan cookies sebelum test
- Pastikan screen resolution konsisten (1920x1080 recommended)
- Gunakan incognito/private browsing mode

### Eksekusi Test
- Ikuti langkah dengan timing yang realistis (jangan terlalu cepat)
- Tunggu loading page selesai sebelum aksi berikutnya
- Perhatikan feedback UI (loading indicators, error messages)
- Test di browser utama: Chrome, Firefox, Safari

### Dokumentasi Bug
- **Judul**: [ID Skenario] - Deskripsi singkat masalah
- **Steps to Reproduce**: Langkah detail untuk replikasi
- **Expected Result**: Hasil yang diharapkan
- **Actual Result**: Hasil yang sebenarnya terjadi
- **Environment**: Browser, OS, timestamp
- **Screenshot/Video**: Evidence visual

## Alur Pengembangan Test

### Siklus Test Development
1. **Requirement Analysis** → Identifikasi skenario test
2. **Manual Test Creation** → Buat skenario manual test
3. **Automated Test Development** → Implementasi Playwright test
4. **Cross Validation** → Validasi konsistensi manual vs automated
5. **Documentation Update** → Update dokumentasi berdasarkan hasil

### Maintenance
- **Review berkala** skenario test sesuai perubahan fitur
- **Update referensi** automated test jika ada perubahan
- **Sync dengan development** untuk fitur baru
- **Archive skenario** yang sudah tidak relevan

---

**Catatan Penting**: Dokumentasi ini adalah living document yang akan terus diperbarui seiring perkembangan aplikasi. Pastikan selalu menggunakan versi terbaru saat melakukan pengujian.