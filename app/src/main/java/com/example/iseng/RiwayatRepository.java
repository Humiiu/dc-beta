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

    public void deleteRiwayatKetring(String id) {
        mDatabase.child("riwayat_ketring").child(id).removeValue();
    }

    public void deleteRiwayatPembayaran(String id) {
        mDatabase.child("riwayat_pembayaran").child(id).removeValue();
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
                long totalKetring = snapshot.child("riwayat_ketring").getChildrenCount();
                
                callback.onDashboardResult(totalSiswa, totalKetring);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                android.util.Log.e("RiwayatRepository", "Firebase error: " + error.getMessage());
            }
        });
    }

    public interface DashboardCallback {
        void onDashboardResult(long totalSiswa, long totalKetring);
    }
}
