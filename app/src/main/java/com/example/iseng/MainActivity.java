package com.example.iseng;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RiwayatRepository repository;
    private TextView tvTotalMurid, tvTotalKetring;
    private LinearLayout llMenuMingguan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser == null) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        repository = new RiwayatRepository();
        
        tvTotalMurid = findViewById(R.id.tvTotalMurid);
        tvTotalKetring = findViewById(R.id.tvTotalKetring);
        llMenuMingguan = findViewById(R.id.llMenuMingguan);

        setupMenuListeners();
        loadDashboardData();
        setupAutoBilling();
        loadWeeklyMenu();
        setupAutoBilling();
        
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmation());
    }

    private void setupAutoBilling() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(AutoBillingWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "AutoBillingWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );

        checkCatchUpBilling();
    }

    private void checkCatchUpBilling() {
        SharedPreferences prefs = getSharedPreferences("DianCateringPrefs", MODE_PRIVATE);
        String lastBillingDateStr = prefs.getString("lastBillingDate", "");
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (lastBillingDateStr.isEmpty()) {
            prefs.edit().putString("lastBillingDate", todayStr).apply();
            return;
        }

        if (todayStr.equals(lastBillingDateStr)) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastDate = sdf.parse(lastBillingDateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastDate);
            cal.add(Calendar.DATE, 1);

            while (sdf.format(cal.getTime()).compareTo(todayStr) < 0) {
                String catchUpDate = sdf.format(cal.getTime());
                AutoBillingWorker.processBillingForDate(catchUpDate);
                cal.add(Calendar.DATE, 1);
            }
            
            prefs.edit().putString("lastBillingDate", todayStr).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMenuListeners() {
        findViewById(R.id.btnDataMurid).setOnClickListener(v -> 
            startActivity(new Intent(this, DaftarSiswaActivity.class))
        );

        findViewById(R.id.btnCatatKetring).setOnClickListener(v -> 
            startActivity(new Intent(this, CatatKetringMassalActivity.class))
        );

        findViewById(R.id.btnBayarMassal).setOnClickListener(v -> 
            startActivity(new Intent(this, BayarMassalActivity.class))
        );

        findViewById(R.id.btnMenuLaporan).setOnClickListener(v -> 
            startActivity(new Intent(this, LaporanJurusanActivity.class))
        );
    }

    private void loadDashboardData() {
        repository.getDashboardData((totalSiswa, totalKetring) -> {
            tvTotalMurid.setText(String.valueOf(totalSiswa));
            tvTotalKetring.setText(String.valueOf(totalKetring));
        });
    }

    private void loadWeeklyMenu() {
        llMenuMingguan.removeAllViews();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("id", "ID"));

        for (int i = 0; i < 5; i++) {
            String dateStr = sdf.format(cal.getTime());
            String dayName = dayFormat.format(cal.getTime()).toUpperCase();
            
            View row = getLayoutInflater().inflate(R.layout.item_menu_mingguan, llMenuMingguan, false);
            TextView tvDay = row.findViewById(R.id.tvMenuDay);
            TextView tvFood = row.findViewById(R.id.tvMenuFood);
            
            tvDay.setText(dayName + " (" + dateStr + ")");
            tvFood.setText("Memuat...");
            
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("menu_mingguan")
                    .child(dateStr).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    String menu = snapshot.getValue(String.class);
                    tvFood.setText(menu != null ? menu : "-");
                }
                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {}
            });

            row.setOnClickListener(v -> showEditMenuDialog(dateStr, dayName));
            llMenuMingguan.addView(row);
            cal.add(Calendar.DATE, 1);
        }
    }

    private void showEditMenuDialog(String date, String day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Isi Menu " + day);
        
        final EditText input = new EditText(this);
        input.setHint("Nama makanan");
        builder.setView(input);

        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("menu_mingguan")
                .child(date).get().addOnSuccessListener(snapshot -> {
                    String currentMenu = snapshot.getValue(String.class);
                    if (currentMenu != null) input.setText(currentMenu);
                });

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String menu = input.getText().toString();
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("menu_mingguan")
                    .child(date).setValue(menu);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("jangan keluar!!")
                .setPositiveButton("yaudah", (dialog, which) -> {
                    mAuth.signOut();
                    goToLogin();
                })
                .setNegativeButton("gajadi", null)
                .show();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (tvTotalMurid != null) {
            loadDashboardData();
        }
    }
}
