package com.example.iseng;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

public class LaporanActivity extends AppCompatActivity {

    private Spinner spnKelas, spnJurusan, spnStatus;
    private RecyclerView rvLaporan;
    private LaporanAdapter adapter;
    
    private List<Siswa> allSiswa = new ArrayList<>();
    private Map<String, List<RiwayatKetring>> allKetring = new HashMap<>();
    private Map<String, List<RiwayatPembayaran>> allPembayaran = new HashMap<>();
    
    private List<LaporanSiswa> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        spnKelas = findViewById(R.id.spnFilterKelasLaporan);
        spnJurusan = findViewById(R.id.spnFilterJurusanLaporan);
        spnStatus = findViewById(R.id.spnFilterStatusLaporan);
        rvLaporan = findViewById(R.id.rvLaporan);

        rvLaporan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(displayList);
        rvLaporan.setAdapter(adapter);

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

        String[] statusOptions = {"Status", "Lunas", "Belum Lunas"};
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, R.layout.spinner_item, statusOptions);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(adapterStatus);

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
        spnStatus.setOnItemSelectedListener(listener);
    }

    private void loadAllData() {
        FirebaseDatabase.getInstance().getReference("siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("LaporanActivity", "Siswa diterima: " + snapshot.getChildrenCount());
                allSiswa.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    allSiswa.add(ds.getValue(Siswa.class));
                }
                loadKetring();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LaporanActivity", "Siswa error: " + error.getMessage());
            }
        });
    }

    private void loadKetring() {
        FirebaseDatabase.getInstance().getReference("riwayat_ketring").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("LaporanActivity", "Ketring diterima: " + snapshot.getChildrenCount());
                allKetring.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RiwayatKetring r = ds.getValue(RiwayatKetring.class);
                    if (r != null) {
                        if (!allKetring.containsKey(r.getSiswaId())) {
                            allKetring.put(r.getSiswaId(), new ArrayList<>());
                        }
                        allKetring.get(r.getSiswaId()).add(r);
                    }
                }
                loadPembayaran();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LaporanActivity", "Ketring error: " + error.getMessage());
            }
        });
    }

    private void loadPembayaran() {
        FirebaseDatabase.getInstance().getReference("riwayat_pembayaran").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("LaporanActivity", "Pembayaran diterima: " + snapshot.getChildrenCount());
                allPembayaran.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RiwayatPembayaran r = ds.getValue(RiwayatPembayaran.class);
                    if (r != null) {
                        if (!allPembayaran.containsKey(r.getSiswaId())) {
                            allPembayaran.put(r.getSiswaId(), new ArrayList<>());
                        }
                        allPembayaran.get(r.getSiswaId()).add(r);
                    }
                }
                processData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LaporanActivity", "Pembayaran error: " + error.getMessage());
            }
        });
    }

    private void processData() {
        try {
            displayList.clear();
            String selKelas = spnKelas.getSelectedItem().toString();
            String selJurusan = spnJurusan.getSelectedItem().toString();
            String selStatus = spnStatus.getSelectedItem().toString();

            Log.d("LaporanActivity", "Filter: " + selKelas + ", " + selJurusan + ", " + selStatus);

            for (Siswa s : allSiswa) {
                boolean matchesKelas = selKelas.equals("Kelas") || s.getKelas().equals(selKelas);
                boolean matchesJurusan = selJurusan.equals("Jurusan") || s.getJurusan().equals(selJurusan);

                if (matchesKelas && matchesJurusan) {
                    List<RiwayatKetring> kList = allKetring.getOrDefault(s.getId(), new ArrayList<>());
                    List<RiwayatPembayaran> pList = allPembayaran.getOrDefault(s.getId(), new ArrayList<>());

                    long totalTagihan = RiwayatRepository.calculateTotalTagihan(kList);
                    long totalDibayar = RiwayatRepository.calculateTotalDibayar(pList);

                    LaporanSiswa ls = new LaporanSiswa(s, totalTagihan, totalDibayar);

                    boolean matchesStatus = selStatus.equals("Status") || ls.getStatus().equals(selStatus);

                    if (matchesStatus) {
                        displayList.add(ls);
                    }
                }
            }
            Log.d("LaporanActivity", "Data diproses: " + displayList.size());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("LaporanActivity", "Error processing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
