package com.example.iseng;

public class Siswa {
    private String id;
    private String nama;
    private String kelas;
    private String jurusan;

    public Siswa() {
        // Required for Firebase
    }

    public Siswa(String id, String nama, String kelas, String jurusan) {
        this.id = id;
        this.nama = nama;
        this.kelas = kelas;
        this.jurusan = jurusan;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
}
