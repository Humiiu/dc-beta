package com.example.iseng;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LaporanTanggalActivity extends AppCompatActivity {

    private String jurusan;
    private RecyclerView rvTanggal;
    private List<String> listTanggal = new ArrayList<>();
    private TanggalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_tanggal);

        jurusan = getIntent().getStringExtra("jurusan");
        if (jurusan == null) {
            finish();
            return;
        }

        TextView tvHeader = findViewById(R.id.tvHeaderJurusan);
        tvHeader.setText("LAPORAN " + jurusan);

        rvTanggal = findViewById(R.id.rvTanggal);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvTanggal.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TanggalAdapter(listTanggal, date -> {
            Intent intent = new Intent(this, LaporanDetailTanggalActivity.class);
            intent.putExtra("jurusan", jurusan);
            intent.putExtra("tanggal", date);
            startActivity(intent);
        });
        rvTanggal.setAdapter(adapter);

        loadTanggalData();
    }

    private void loadTanggalData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        
        db.child("siswa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot siswaSnapshot) {
                Set<String> siswaIdsInJurusan = new HashSet<>();
                for (DataSnapshot ds : siswaSnapshot.getChildren()) {
                    Siswa s = ds.getValue(Siswa.class);
                    if (s != null && jurusan.equals(s.getJurusan())) {
                        siswaIdsInJurusan.add(s.getId());
                    }
                }
                
                db.child("riwayat_ketring").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot riwayatSnapshot) {
                        Set<String> tanggalSet = new HashSet<>();
                        for (DataSnapshot ds : riwayatSnapshot.getChildren()) {
                            RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                            if (r != null && siswaIdsInJurusan.contains(r.getSiswaId())) {
                                tanggalSet.add(r.getTanggal());
                            }
                        }
                        
                        listTanggal.clear();
                        listTanggal.addAll(tanggalSet);
                        Collections.sort(listTanggal, Collections.reverseOrder());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private static class TanggalAdapter extends RecyclerView.Adapter<TanggalAdapter.ViewHolder> {
        private List<String> list;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String date);
        }

        public TanggalAdapter(List<String> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan_tanggal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String dateStr = list.get(position);
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
                holder.tvTanggal.setText(displayFormat.format(date));
            } catch (Exception e) {
                holder.tvTanggal.setText(dateStr);
            }

            holder.tvMenu.setText("Memuat menu...");
            FirebaseDatabase.getInstance().getReference("menu_mingguan").child(dateStr)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String menu = snapshot.getValue(String.class);
                    holder.tvMenu.setText("Menu: " + (menu != null ? menu : "Menu belum diisi"));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            holder.itemView.setOnClickListener(v -> listener.onItemClick(dateStr));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTanggal, tvMenu;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTanggal = itemView.findViewById(R.id.tvLaporanTanggal);
                tvMenu = itemView.findViewById(R.id.tvLaporanMenu);
            }
        }
    }
}
