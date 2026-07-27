package com.example.iseng;

public class RiwayatPembayaran {
    private String id;
    private String siswaId;
    private String tanggal;
    private long nominal;
    private String adminEmail;

    public RiwayatPembayaran() {
        // Required for Firebase
    }

    public RiwayatPembayaran(String id, String siswaId, String tanggal, long nominal, String adminEmail) {
        this.id = id;
        this.siswaId = siswaId;
        this.tanggal = tanggal;
        this.nominal = nominal;
        this.adminEmail = adminEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSiswaId() { return siswaId; }
    public void setSiswaId(String siswaId) { this.siswaId = siswaId; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public long getNominal() { return nominal; }
    public void setNominal(long nominal) { this.nominal = nominal; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}
