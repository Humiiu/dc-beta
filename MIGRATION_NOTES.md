# Catatan Migrasi Skema Data

Aplikasi ini telah diperbarui ke skema data berbasis riwayat (history-based).

## Perubahan Struktur Firebase

1.  **Node `/siswa/{id}`**:
    *   Field `totalKatering`, `totalBayar`, `statusBayar`, dan `lastPaymentDate` **sudah tidak digunakan lagi** oleh kode baru.
    *   Data profil (`nama`, `kelas`, `jurusan`) tetap dipertahankan.
    *   Disarankan untuk menghapus field yang tidak terpakai secara manual melalui Firebase Console untuk membersihkan database.

2.  **Node Baru `/riwayat_ketring`**:
    *   Menyimpan setiap transaksi ketring harian.
    *   Field: `siswaId`, `tanggal`, `harga`, `adminEmail`.

3.  **Node Baru `/riwayat_pembayaran`**:
    *   Menyimpan setiap riwayat pembayaran/cicilan.
    *   Field: `siswaId`, `tanggal`, `nominal`, `adminEmail`.

4.  **Node Baru `/settings/hargaKetringDefault`**:
    *   Menyimpan harga default yang bisa diubah melalui menu Pengaturan.

## Dampak pada Data Lama

*   Total tagihan dan status lunas untuk siswa lama akan mulai dari **Rp 0** di aplikasi baru karena data riwayat sebelumnya tidak dimigrasi secara otomatis.
*   Admin harus mencatat ulang ketring/pembayaran jika ingin data siswa lama sinkron dengan skema baru, atau melakukan migrasi data manual jika diperlukan.
