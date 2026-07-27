package com.example.iseng;

public class RiwayatKetring {
    private String id;
    private String siswaId;
    private String tanggal;
    private long harga;
    private String adminEmail;

    public RiwayatKetring() {
        // Required for Firebase
    }

    public RiwayatKetring(String id, String siswaId, String tanggal, long harga, String adminEmail) {
        this.id = id;
        this.siswaId = siswaId;
        this.tanggal = tanggal;
        this.harga = harga;
        this.adminEmail = adminEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSiswaId() { return siswaId; }
    public void setSiswaId(String siswaId) { this.siswaId = siswaId; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}
