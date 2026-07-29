package com.example.iseng;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RiwayatRepository repository;
    private TextView tvTotalMurid, tvBelumLunas;
    private LinearLayout llLaporanPreview;

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
        tvBelumLunas = findViewById(R.id.tvBelumLunas);
        llLaporanPreview = findViewById(R.id.llLaporanPreview);

        setupMenuListeners();
        loadDashboardData();
        
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmation());
    }

    private void setupMenuListeners() {
        findViewById(R.id.btnDataMurid).setOnClickListener(v -> 
            startActivity(new Intent(this, DaftarSiswaActivity.class))
        );

        findViewById(R.id.btnCatatKetring).setOnClickListener(v -> 
            startActivity(new Intent(this, CatatKetringMassalActivity.class))
        );

        findViewById(R.id.btnMenuLaporan).setOnClickListener(v -> 
            startActivity(new Intent(this, LaporanActivity.class))
        );

        findViewById(R.id.btnLihatSemuaLaporan).setOnClickListener(v -> 
            startActivity(new Intent(this, DetailJurusanActivity.class))
        );
    }

    private void loadDashboardData() {
        repository.getDashboardData((totalSiswa, belumLunas, topJurusans) -> {
            tvTotalMurid.setText(String.valueOf(totalSiswa));
            tvBelumLunas.setText(String.valueOf(belumLunas));
            
            populateLaporanPreview(topJurusans);
        });
    }

    private void populateLaporanPreview(List<RiwayatRepository.UnpaidJurusan> topJurusans) {
        llLaporanPreview.removeAllViews();
        if (topJurusans == null || topJurusans.isEmpty()) {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("horee lunas semua (keren gini jir)");
            emptyTv.setTextColor(Color.parseColor("#F3E6D8"));
            emptyTv.setTextSize(14);
            llLaporanPreview.addView(emptyTv);
            return;
        }

        for (int i = 0; i < topJurusans.size(); i++) {
            RiwayatRepository.UnpaidJurusan item = topJurusans.get(i);
            
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 16, 0, 16);
            
            TextView tvJurusan = new TextView(this);
            tvJurusan.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvJurusan.setText(item.nama);
            tvJurusan.setTextColor(Color.WHITE);
            tvJurusan.setTextSize(14);
            
            TextView tvCount = new TextView(this);
            String countText = item.count + " orang belum lunas";
            tvCount.setText(countText);
            tvCount.setTextColor(Color.parseColor("#CCFFFFFF"));
            tvCount.setTextSize(13);
            
            row.addView(tvJurusan);
            row.addView(tvCount);
            
            llLaporanPreview.addView(row);
            
            // Add divider except for the last item
            if (i < topJurusans.size() - 1) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                llLaporanPreview.addView(divider);
            }
        }
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
