package com.example.iseng;

public class LaporanSiswa {
    private Siswa siswa;
    private long totalTagihan;
    private long totalDibayar;

    public LaporanSiswa(Siswa siswa, long totalTagihan, long totalDibayar) {
        this.siswa = siswa;
        this.totalTagihan = totalTagihan;
        this.totalDibayar = totalDibayar;
    }

    public Siswa getSiswa() { return siswa; }
    public long getTotalTagihan() { return totalTagihan; }
    public long getTotalDibayar() { return totalDibayar; }
    public long getSisa() { return totalTagihan - totalDibayar; }
    public String getStatus() {
        return (getSisa() <= 0 && totalTagihan > 0) ? "Lunas" : "Belum Lunas";
    }
}
