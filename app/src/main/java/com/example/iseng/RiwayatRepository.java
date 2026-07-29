package com.example.iseng;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiwayatRepository {

    public static final long HARGA_KETRING = 20000;
    private final DatabaseReference mDatabase;

    public RiwayatRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addRiwayatKetring(RiwayatKetring record) {
        String id = mDatabase.child("riwayat_ketring").push().getKey();
        if (id != null) {
            record.setId(id);
            mDatabase.child("riwayat_ketring").child(id).setValue(record);
        }
    }

    public void addRiwayatPembayaran(RiwayatPembayaran record) {
        String id = mDatabase.child("riwayat_pembayaran").push().getKey();
        if (id != null) {
            record.setId(id);
            mDatabase.child("riwayat_pembayaran").child(id).setValue(record);
        }
    }

    public void getRiwayatKetringBySiswa(String siswaId, ValueEventListener listener) {
        mDatabase.child("riwayat_ketring").orderByChild("siswaId").equalTo(siswaId).addValueEventListener(listener);
    }

    public void getRiwayatPembayaranBySiswa(String siswaId, ValueEventListener listener) {
        mDatabase.child("riwayat_pembayaran").orderByChild("siswaId").equalTo(siswaId).addValueEventListener(listener);
    }
    
    public static long calculateTotalTagihan(List<RiwayatKetring> list) {
        long total = 0;
        if (list != null) {
            for (RiwayatKetring r : list) {
                total += r.getHarga();
            }
        }
        return total;
    }

    public static long calculateTotalDibayar(List<RiwayatPembayaran> list) {
        long total = 0;
        if (list != null) {
            for (RiwayatPembayaran r : list) {
                total += r.getNominal();
            }
        }
        return total;
    }

    public void getDashboardData(DashboardCallback callback) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long totalSiswa = snapshot.child("siswa").getChildrenCount();
                
                Map<String, Long> tagihanMap = new HashMap<>();
                for (DataSnapshot ds : snapshot.child("riwayat_ketring").getChildren()) {
                    RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                    if (r != null) {
                        tagihanMap.put(r.getSiswaId(), tagihanMap.getOrDefault(r.getSiswaId(), 0L) + r.getHarga());
                    }
                }

                Map<String, Long> bayarMap = new HashMap<>();
                for (DataSnapshot ds : snapshot.child("riwayat_pembayaran").getChildren()) {
                    RiwayatPembayaran r = ds.getValue(RiwayatPembayaran.class);
                    if (r != null) {
                        bayarMap.put(r.getSiswaId(), bayarMap.getOrDefault(r.getSiswaId(), 0L) + r.getNominal());
                    }
                }

                long belumLunasCount = 0;
                Map<String, Integer> unpaidByJurusan = new HashMap<>();
                
                for (DataSnapshot ds : snapshot.child("siswa").getChildren()) {
                    Siswa s = ds.getValue(Siswa.class);
                    if (s != null) {
                        String id = s.getId();
                        long tagihan = tagihanMap.getOrDefault(id, 0L);
                        long bayar = bayarMap.getOrDefault(id, 0L);
                        if (tagihan > bayar) {
                            belumLunasCount++;
                            String jurusan = s.getJurusan();
                            unpaidByJurusan.put(jurusan, unpaidByJurusan.getOrDefault(jurusan, 0) + 1);
                        }
                    }
                }
                
                List<Map.Entry<String, Integer>> sortedJurusans = new ArrayList<>(unpaidByJurusan.entrySet());
                sortedJurusans.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                
                List<UnpaidJurusan> topJurusans = new ArrayList<>();
                for (int i = 0; i < Math.min(3, sortedJurusans.size()); i++) {
                    Map.Entry<String, Integer> entry = sortedJurusans.get(i);
                    topJurusans.add(new UnpaidJurusan(entry.getKey(), entry.getValue()));
                }
                
                callback.onDashboardResult(totalSiswa, belumLunasCount, topJurusans);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                android.util.Log.e("RiwayatRepository", "Firebase error: " + error.getMessage());
            }
        });
    }

    public interface DashboardCallback {
        void onDashboardResult(long totalSiswa, long belumLunas, List<UnpaidJurusan> topJurusans);
    }

    public static class UnpaidJurusan {
        public String nama;
        public int count;
        public UnpaidJurusan(String nama, int count) {
            this.nama = nama;
            this.count = count;
        }
    }
}
