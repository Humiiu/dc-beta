package com.example.iseng;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LaporanDetailTanggalActivity extends AppCompatActivity {

    private String jurusan, tanggal;
    private RecyclerView rvDetail;
    private EditText etSearch;
    private TextView tvTotal;
    private DetailAdapter adapter;
    private List<Siswa> allSiswaList = new ArrayList<>();
    private List<Siswa> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_detail_tanggal);

        jurusan = getIntent().getStringExtra("jurusan");
        tanggal = getIntent().getStringExtra("tanggal");

        if (jurusan == null || tanggal == null) {
            finish();
            return;
        }

        TextView tvHeader = findViewById(R.id.tvHeaderTanggal);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(tanggal);
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
            tvHeader.setText(displayFormat.format(date));
        } catch (Exception e) {
            tvHeader.setText(tanggal);
        }

        rvDetail = findViewById(R.id.rvDetailTanggal);
        etSearch = findViewById(R.id.etSearchLaporan);
        tvTotal = findViewById(R.id.tvTotalLaporan);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(filteredList);
        rvDetail.setAdapter(adapter);

        setupSearch();
        loadDetailData();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDetailData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        
        db.child("siswa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot siswaSnapshot) {
                Map<String, Siswa> siswaMap = new HashMap<>();
                for (DataSnapshot ds : siswaSnapshot.getChildren()) {
                    Siswa s = ds.getValue(Siswa.class);
                    if (s != null && jurusan.equals(s.getJurusan())) {
                        siswaMap.put(s.getId(), s);
                    }
                }
                
                db.child("riwayat_ketring").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot riwayatSnapshot) {
                        allSiswaList.clear();
                        for (DataSnapshot ds : riwayatSnapshot.getChildren()) {
                            RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                            if (r != null && tanggal.equals(r.getTanggal()) && siswaMap.containsKey(r.getSiswaId())) {
                                allSiswaList.add(siswaMap.get(r.getSiswaId()));
                            }
                        }
                        applyFilter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void applyFilter() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        filteredList.clear();
        for (Siswa s : allSiswaList) {
            if (s.getNama().toLowerCase().contains(query)) {
                filteredList.add(s);
            }
        }
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        long total = filteredList.size() * RiwayatRepository.HARGA_KETRING;
        tvTotal.setText(formatRupiah(total));
    }

    private String formatRupiah(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }

    private static class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
        private List<Siswa> list;

        public DetailAdapter(List<Siswa> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_siswa, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Siswa s = list.get(position);
            holder.tvNama.setText(s.getNama());
            holder.tvKelas.setText(s.getKelas() + " - " + s.getJurusan());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvKelas;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvNama);
                tvKelas = itemView.findViewById(R.id.tvKelas);
            }
        }
    }
}
