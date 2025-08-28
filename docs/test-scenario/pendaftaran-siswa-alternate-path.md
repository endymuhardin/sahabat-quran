# Skenario Pengujian: Pendaftaran Siswa - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling
- **Modul**: Pendaftaran Siswa
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Automated Test**: `StudentRegistrationPlaywrightTest.java`
- **Total Skenario**: 5 skenario validasi

---

## PS-AP-001: Validasi Field Required

### Informasi Skenario
- **ID Skenario**: PS-AP-001 (Pendaftaran Siswa - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Playwright Method**: `shouldValidateRequiredFieldsAndPreventEmptySubmission()`
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Aplikasi dapat diakses di `http://localhost:8080`
- Browser dalam mode incognito/private

### Data Test
```
Skenario: Form kosong (tidak mengisi field apapun)
```

### Langkah Pengujian

1. **Akses form pendaftaran**
   - Aksi: Buka `/register`
   - Verifikasi: Form pendaftaran muncul dengan semua field kosong

2. **Coba submit tanpa mengisi form**
   - Aksi: Scroll ke bawah dan langsung klik "Submit Pendaftaran"
   - Verifikasi: 
     - Form tidak ter-submit
     - Tetap di halaman `/register`
     - Pesan validasi muncul untuk field required
     - Tidak ada redirect ke halaman konfirmasi

3. **Verifikasi pesan validasi**
   - Verifikasi:
     - Field nama lengkap menunjukkan error "Nama lengkap wajib diisi"
     - Field email menunjukkan error "Email wajib diisi"
     - Field telepon menunjukkan error "Nomor telepon wajib diisi"
     - Field program menunjukkan error "Program wajib dipilih"

### Hasil Diharapkan
- Form tidak ter-submit ke server
- Validasi client-side mencegah submission
- Pesan error informatif dan jelas
- User tetap di halaman form dengan highlight field yang error

### Kriteria Sukses
- [ ] Form tidak ter-submit dengan field kosong
- [ ] Pesan error muncul untuk setiap field required
- [ ] URL tetap `/register` (tidak ada redirect)
- [ ] UI menunjukkan field yang error dengan jelas

---

## PS-AP-002: Validasi Email Duplikat

### Informasi Skenario
- **ID Skenario**: PS-AP-002
- **Prioritas**: Tinggi
- **Playwright Method**: `shouldPreventDuplicateEmailRegistration()`
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Database sudah memiliki data email yang akan ditest
- Browser dalam mode incognito/private

### Data Test
```
Pendaftaran Pertama:
Email: duplicate.test@example.com
Telepon: 081111111111

Pendaftaran Kedua (Duplikat):
Email: duplicate.test@example.com (SAMA dengan yang pertama)
Telepon: 081222222222 (berbeda)
```

### Langkah Pengujian

#### Bagian 1: Buat Pendaftaran Pertama
1. **Isi form pendaftaran lengkap pertama**
   - Aksi: Isi semua field required dengan data test pertama
   - Verifikasi: Form dapat diisi tanpa error

2. **Submit pendaftaran pertama**
   - Aksi: Submit form
   - Verifikasi: Pendaftaran berhasil, redirect ke konfirmasi

#### Bagian 2: Coba Pendaftaran Duplikat
3. **Akses form pendaftaran baru**
   - Aksi: Buka `/register` di tab/session baru
   - Verifikasi: Form kosong siap untuk input

4. **Isi form dengan email yang sama**
   - Aksi: Isi semua field required tapi gunakan email yang sudah terdaftar
   - Verifikasi: Form dapat diisi tanpa warning (belum ada validasi real-time)

5. **Submit form duplikat**
   - Aksi: Klik "Submit Pendaftaran"
   - Verifikasi:
     - Form tidak berhasil ter-submit
     - Muncul pesan error "Email sudah terdaftar"
     - Tetap di halaman `/register`
     - Field email di-highlight sebagai error

### Hasil Diharapkan
- Sistem mendeteksi email duplikat
- Pesan error yang jelas: "Email sudah terdaftar, silakan gunakan email lain"
- User diminta menggunakan email berbeda
- Form tetap mempertahankan data yang sudah diisi (kecuali email)

### Kriteria Sukses
- [ ] Pendaftaran pertama dengan email berhasil
- [ ] Pendaftaran kedua dengan email sama ditolak
- [ ] Pesan error duplikat email muncul dengan jelas
- [ ] Data lain di form tidak hilang saat error

---

## PS-AP-003: Validasi Nomor Telepon Duplikat

### Informasi Skenario
- **ID Skenario**: PS-AP-003
- **Prioritas**: Tinggi
- **Playwright Method**: `shouldPreventDuplicatePhoneRegistration()`
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Database sudah memiliki data telepon yang akan ditest

### Data Test
```
Pendaftaran Pertama:
Email: first.test@example.com
Telepon: 081333333333

Pendaftaran Kedua (Duplikat):
Email: second.test@example.com (berbeda)
Telepon: 081333333333 (SAMA dengan yang pertama)
```

### Langkah Pengujian

1. **Buat pendaftaran dengan nomor telepon tertentu**
   - Aksi: Isi dan submit pendaftaran dengan data test pertama
   - Verifikasi: Pendaftaran berhasil

2. **Coba daftar dengan nomor telepon yang sama**
   - Aksi: Buat pendaftaran baru dengan email berbeda tapi telepon sama
   - Verifikasi: 
     - Muncul pesan "Nomor telepon sudah terdaftar"
     - Form tidak ter-submit
     - Field telepon di-highlight error

### Kriteria Sukses
- [ ] Sistem mendeteksi duplikat nomor telepon
- [ ] Pesan error yang informatif muncul
- [ ] Pendaftaran dengan telepon duplikat ditolak

---

## PS-AP-004: Validasi Format Email

### Informasi Skenario
- **ID Skenario**: PS-AP-004
- **Prioritas**: Sedang
- **Playwright Method**: `shouldValidateEmailFormat()`
- **Estimasi Waktu**: 3-4 menit

### Data Test
```
Email Invalid:
- "invalid-email" (tanpa @)
- "test@" (tanpa domain)
- "@example.com" (tanpa username)
- "test..test@example.com" (double dot)
```

### Langkah Pengujian

1. **Test berbagai format email invalid**
   - Aksi: Isi field nama lengkap dan email dengan format invalid
   - Aksi: Coba submit form
   - Verifikasi:
     - Browser validation muncul untuk format email
     - Atau server validation menolak dengan pesan error
     - Form tidak ter-submit

2. **Verifikasi pesan error email**
   - Verifikasi: Pesan error menjelaskan format email yang benar

### Kriteria Sukses
- [ ] Format email invalid ditolak sistem
- [ ] Pesan error menjelaskan format yang benar
- [ ] Validasi bisa client-side atau server-side

---

## PS-AP-005: Validasi Format Nomor Telepon

### Informasi Skenario
- **ID Skenario**: PS-AP-005
- **Prioritas**: Sedang
- **Playwright Method**: `shouldValidatePhoneNumberFormat()`
- **Estimasi Waktu**: 3-4 menit

### Data Test
```
Nomor Telepon Invalid:
- "123" (terlalu pendek)
- "abc123def" (mengandung huruf)
- "021-1234-5678" (format dengan dash)
- "+62-812-3456-7890" (format internasional)
```

### Langkah Pengujian

1. **Test format nomor telepon pendek**
   - Aksi: Isi nomor telepon dengan "123"
   - Aksi: Isi field wajib lainnya
   - Aksi: Coba submit
   - Verifikasi: Error muncul untuk format telepon

2. **Test nomor telepon dengan karakter non-digit**
   - Aksi: Isi nomor telepon dengan "abc123def"
   - Verifikasi: Validasi menolak input atau show error

3. **Verifikasi pesan error**
   - Verifikasi: 
     - Pesan error jelas: "Format nomor telepon tidak valid"
     - Contoh format yang benar ditampilkan: "Contoh: 081234567890"

### Kriteria Sukses
- [ ] Format telepon invalid ditolak
- [ ] Pesan error informatif dengan contoh format
- [ ] Validasi consistent untuk berbagai format invalid

---

## PS-AP-006: Validasi Program Selection Required

### Informasi Skenario
- **ID Skenario**: PS-AP-006
- **Prioritas**: Sedang
- **Estimasi Waktu**: 2-3 menit

### Langkah Pengujian

1. **Isi form tanpa memilih program**
   - Aksi: Isi semua field required kecuali program
   - Aksi: Submit form
   - Verifikasi: Error "Program wajib dipilih" muncul

2. **Isi form tanpa memilih jadwal**
   - Aksi: Pilih program tapi tidak pilih jadwal
   - Verifikasi: Error "Jadwal wajib dipilih" muncul

### Kriteria Sukses
- [ ] Validasi program selection berfungsi
- [ ] Validasi jadwal selection berfungsi
- [ ] Pesan error sesuai konteks

---

## PS-AP-007: Error Handling untuk Server Issues

### Informasi Skenario
- **ID Skenario**: PS-AP-007
- **Prioritas**: Rendah
- **Estimasi Waktu**: 2-3 menit (jika server error dapat disimulasi)

### Langkah Pengujian

1. **Simulasi server error**
   - Kondisi: Server database mati atau aplikasi error
   - Aksi: Coba submit form yang valid
   - Verifikasi: 
     - Pesan error server muncul
     - User tidak kehilangan data yang sudah diisi
     - Ada instruksi untuk retry

### Kriteria Sukses
- [ ] Error server ditangani dengan graceful
- [ ] Pesan error user-friendly
- [ ] Data form tidak hilang

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/StudentRegistrationPlaywrightTest.java`

### Method Mapping
- **PS-AP-001**: `shouldValidateRequiredFieldsAndPreventEmptySubmission()`
- **PS-AP-002**: `shouldPreventDuplicateEmailRegistration()`
- **PS-AP-003**: `shouldPreventDuplicatePhoneRegistration()`
- **PS-AP-004**: `shouldValidateEmailFormat()`
- **PS-AP-005**: `shouldValidatePhoneNumberFormat()`

### Eksekusi Automated Test
```bash
# Jalankan validation tests
./mvnw test -Dtest="StudentRegistrationPlaywrightTest"

# Dengan debugging
./mvnw test -Dtest="StudentRegistrationPlaywrightTest" -Dplaywright.debug.enabled=true
```

### Catatan untuk Tester

#### Focus Areas
- **User Experience**: Bagaimana user bereaksi terhadap error messages
- **Error Message Clarity**: Apakah pesan error mudah dipahami
- **Form State**: Apakah data yang sudah diisi hilang saat error
- **Loading Indicators**: Apakah ada feedback saat proses validasi

#### Browser Testing
- Test di berbagai browser untuk konsistensi validasi client-side
- Perhatikan perbedaan HTML5 validation behavior
- Test dengan JavaScript disabled untuk server-side validation

#### Edge Cases
- Copy-paste data dengan format aneh
- Input dengan karakter special/unicode
- Form submission dengan koneksi internet lambat
- Multiple tab dengan session yang sama