package com.example.iseng;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AutoBillingWorker extends Worker {

    public AutoBillingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        processBillingForDate(today);
        return Result.success();
    }

    public static void processBillingForDate(String date) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        
        db.child("siswa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Siswa s = ds.getValue(Siswa.class);
                    if (s != null && s.isLangganan()) {
                        checkAndAddBilling(s.getId(), date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AutoBillingWorker", "Error fetching students: " + error.getMessage());
            }
        });
    }

    private static void checkAndAddBilling(String siswaId, String date) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        
        db.child("riwayat_ketring")
                .orderByChild("siswaId")
                .equalTo(siswaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyBilled = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                    if (r != null && date.equals(r.getTanggal())) {
                        alreadyBilled = true;
                        break;
                    }
                }

                if (!alreadyBilled) {
                    RiwayatRepository repo = new RiwayatRepository();
                    RiwayatKetring record = new RiwayatKetring(null, siswaId, date, RiwayatRepository.HARGA_KETRING, "system-auto");
                    repo.addRiwayatKetring(record);
                    Log.d("AutoBillingWorker", "Added billing for student: " + siswaId + " on date: " + date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AutoBillingWorker", "Error checking billing: " + error.getMessage());
            }
        });
    }
}
