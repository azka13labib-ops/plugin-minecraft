# DemoPlugin - Toko & Scoreboard Kustom (Paper 1.21)

DemoPlugin adalah plugin Minecraft survival-economy kustom yang dikembangkan khusus untuk **Paper 1.21**. Plugin ini mengintegrasikan menu toko visual (GUI) yang profesional, sistem penjualan cepat, papan statistik (Scoreboard), pencatatan transaksi ke database, serta penyembunyi perintah otomatis untuk menjaga kerapian obrolan server.

---

## 🚀 Fitur Utama

### 1. Menu Toko Kustom (Visual GUI)
*   **Menu Kategori**: Terbagi ke dalam 4 kategori utama yang estetik:
    *   `Minerals`: Menjual bijih besi, emas, diamond, netherite, dsb.
    *   `Farming`: Menjual gandum, kentang, wortel, melon, roti, dsb.
    *   `Wood & Nature`: Menjual log kayu, papan, buku, kertas, dsb.
    *   `Combat Drops`: Menjual drop monster seperti blaze rod, ender pearl, nether star, dsb.
*   **Tampilan Premium (Modal Tooltip)**: Lore item didesain rapi, berjarak, kontras, serta dilengkapi dengan petunjuk interaksi klik (Klik Kiri untuk 1, Klik Reruntuk 64, Shift+Kiri untuk Jual Semua).
*   **Fitur Jual Semua (Universal Sell-All)**: Memindai seluruh inventory secara instan, menyaring barang toko, menampilkan estimasi total pendapatan, dan memerlukan konfirmasi aman sebelum uang dicairkan.

### 2. Papan Statistik (Scoreboard Sidebar)
*   Menampilkan data real-time: Nama Pemain, Ping, Pekerjaan Aktif, dan Uang.
*   Pembaruan instan pasca-transaksi dan otomatis berjalan di latar belakang setiap 5 detik.

### 3. Keamanan Ekonomi (Anti-Exploit)
*   Telah diaudit untuk mencegah eksploitasi ekonomi melalui proses crafting/pembelian bahan mentah.
*   Harga jual **Oak Planks** diturunkan, **Stick** tidak bisa dijual, dan harga jual **Blaze Powder** dikurangi untuk menghindari sirkulasi uang tanpa batas.

### 4. Penyembunyi Perintah (Command Hider)
*   Menyembunyikan perintah admin dan sistem (`/ah`, `/jobs`, `/tab`, `/papi`, dll.) serta perintah berformat namespace (`/essentials:fly`, dll.) dari tab-complete pemain non-OP agar tampilan konsol chat server bersih.

### 5. Asynchronous Logging Database
*   Setiap pembelian diamond dicatat ke database SQLite/MySQL secara asinkron agar thread utama server tidak mengalami lag.

---

## 🛠️ Persyaratan & Dependensi
Pastikan server Anda memiliki plugin-plugin berikut di folder `plugins/`:
1.  **Vault** - Menghubungkan perekonomian.
2.  **EssentialsX** - Sebagai sistem ekonomi dasar.
3.  **EconomyShopGUI** - Integrasi transaksi (opsional).
4.  **TAB** - Menampilkan scoreboard.
5.  **PlaceholderAPI (PAPI)** - Menerjemahkan kode statistik di scoreboard.
6.  **Jobs Reborn & CMILib** - Mengaktifkan fitur sistem pekerjaan.
7.  **AzAuctions** - Mengaktifkan fitur pelelangan barang (`/ah`).

---

## 📝 Daftar Perintah (Commands)
*   **`/shop`** ➔ Membuka Menu Utama Toko Azka.
*   **`/shop sell`** ➔ Membuka menu konfirmasi Jual Semua.

---

## 🛠️ Instalasi & Build
Gunakan Maven untuk mengompilasi plugin ini:
```bash
mvn clean package
```
Hasil kompilasi `.jar` berada di folder `target/` dan siap dipindahkan ke folder `plugins/` server Anda.
