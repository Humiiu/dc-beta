package com.example.iseng;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DetailJurusanActivity extends AppCompatActivity {

    private Spinner spnKelas, spnJurusan;
    private RecyclerView rvDetail;
    private DetailJurusanAdapter adapter;
    
    private List<Siswa> allSiswa = new ArrayList<>();
    private Map<String, List<RiwayatKetring>> allKetring = new HashMap<>();
    private Map<String, List<RiwayatPembayaran>> allPembayaran = new HashMap<>();
    
    private List<JurusanGroup> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jurusan);

        spnKelas = findViewById(R.id.spnFilterKelas);
        spnJurusan = findViewById(R.id.spnFilterJurusan);
        rvDetail = findViewById(R.id.rvDetailJurusan);

        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailJurusanAdapter(displayList);
        rvDetail.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupFilters();
        loadAllData();
    }

    private void setupFilters() {
        String[] kelasOptions = {"Kelas", "10", "11", "12"};
        ArrayAdapter<String> adapterKelas = new ArrayAdapter<>(this, R.layout.spinner_item, kelasOptions);
        adapterKelas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnKelas.setAdapter(adapterKelas);

        String[] jurusanOptions = {"Jurusan", "RPL", "TKJ", "BC", "DKV", "GD", "ANM"};
        ArrayAdapter<String> adapterJurusan = new ArrayAdapter<>(this, R.layout.spinner_item, jurusanOptions);
        adapterJurusan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJurusan.setAdapter(adapterJurusan);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                processData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spnKelas.setOnItemSelectedListener(listener);
        spnJurusan.setOnItemSelectedListener(listener);
    }

    private void loadAllData() {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSiswa.clear();
                for (DataSnapshot ds : snapshot.child("siswa").getChildren()) {
                    Siswa s = ds.getValue(Siswa.class);
                    if (s != null) allSiswa.add(s);
                }

                allKetring.clear();
                for (DataSnapshot ds : snapshot.child("riwayat_ketring").getChildren()) {
                    RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                    if (r != null) {
                        allKetring.computeIfAbsent(r.getSiswaId(), k -> new ArrayList<>()).add(r);
                    }
                }

                allPembayaran.clear();
                for (DataSnapshot ds : snapshot.child("riwayat_pembayaran").getChildren()) {
                    RiwayatPembayaran r = ds.getValue(RiwayatPembayaran.class);
                    if (r != null) {
                        allPembayaran.computeIfAbsent(r.getSiswaId(), k -> new ArrayList<>()).add(r);
                    }
                }
                processData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailJurusanActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData() {
        displayList.clear();
        String selKelas = spnKelas.getSelectedItem().toString();
        String selJurusan = spnJurusan.getSelectedItem().toString();

        Map<String, JurusanGroup> groups = new TreeMap<>();

        for (Siswa s : allSiswa) {
            boolean matchesKelas = selKelas.equals("Kelas") || s.getKelas().equals(selKelas);
            boolean matchesJurusan = selJurusan.equals("Jurusan") || s.getJurusan().equals(selJurusan);

            if (matchesKelas && matchesJurusan) {
                String key = s.getJurusan() + " - Kelas " + s.getKelas();
                JurusanGroup group = groups.computeIfAbsent(key, k -> new JurusanGroup(s.getJurusan(), s.getKelas()));
                
                group.totalStudents++;
                
                List<RiwayatKetring> kList = allKetring.getOrDefault(s.getId(), new ArrayList<>());
                List<RiwayatPembayaran> pList = allPembayaran.getOrDefault(s.getId(), new ArrayList<>());
                
                long totalTagihan = RiwayatRepository.calculateTotalTagihan(kList);
                long totalDibayar = RiwayatRepository.calculateTotalDibayar(pList);
                
                if (totalTagihan > totalDibayar) {
                    group.unpaidStudents++;
                }
            }
        }

        displayList.addAll(groups.values());
        adapter.notifyDataSetChanged();
    }

    public static class JurusanGroup {
        public String jurusan;
        public String kelas;
        public int totalStudents = 0;
        public int unpaidStudents = 0;

        public JurusanGroup(String jurusan, String kelas) {
            this.jurusan = jurusan;
            this.kelas = kelas;
        }
        
        public String getDisplayName() {
            return jurusan + " - Kelas " + kelas;
        }
    }
}
