# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ) #

Aplikasi berbasis web ini dikembangkan untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Aplikasi ini akan digunakan oleh siswa, pengajar, staf administrasi, staf keuangan, dan manajemen untuk mempermudah proses pendaftaran, pengelolaan kelas, pencatatan kehadiran, keuangan, dan pelaporan.

## Fitur Utama ##

* Manajemen Pengguna: Registrasi siswa baru, pengelolaan profil, dan login berbasis username/password.

* Manajemen Kelas & Jadwal: Pembuatan dan pengelolaan kelas, level, dan jadwal pelajaran.

* Pencatatan Kehadiran: Pengajar dapat mencatat kehadiran siswa per sesi.

* Progres & Penilaian: Pencatatan kemajuan siswa, rapor, serta manajemen ujian teori dan praktik.

* Manajemen Keuangan: Pembuatan tagihan iuran, verifikasi pembayaran, dan perhitungan gaji pengajar.

* Manajemen Event: Pengelolaan event insidental (misalnya kajian), pendaftaran, dan pembayaran.

* Pelaporan & Dashboard: Menyediakan laporan dan dashboard untuk analisis data operasional.

## Teknologi yang Digunakan ##

* Backend: Spring Boot (Versi Terbaru)
* Frontend: Thymeleaf, TailwindCSS, dan JavaScript
* Database: PostgreSQL
* Otentikasi: Spring Security (otentikasi standar berbasis username/password)
* Deployment: DigitalOcean

## Persyaratan Sistem ##

Pastikan Anda memiliki perangkat lunak berikut terinstal di komputer Anda:

* Java Development Kit (JDK) 21 atau versi yang lebih baru
* Maven 3.9
* PostgreSQL 17
* Git
* Docker (diperlukan untuk menjalankan Testcontainers)
