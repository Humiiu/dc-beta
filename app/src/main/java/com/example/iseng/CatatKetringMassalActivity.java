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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CatatKetringMassalActivity extends AppCompatActivity {

    private Spinner spnFilterKelas, spnFilterJurusan;
    private RecyclerView rvSiswa;
    private SiswaMassalAdapter adapter;
    private List<Siswa> allSiswaList = new ArrayList<>();
    private List<Siswa> filteredList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private RiwayatRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catat_ketring_massal);

        mDatabase = FirebaseDatabase.getInstance().getReference("siswa");
        repository = new RiwayatRepository();

        spnFilterKelas = findViewById(R.id.spnFilterKelasMassal);
        spnFilterJurusan = findViewById(R.id.spnFilterJurusanMassal);
        rvSiswa = findViewById(R.id.rvSiswaMassal);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvSiswa.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SiswaMassalAdapter(filteredList);
        rvSiswa.setAdapter(adapter);

        setupFilters();
        loadSiswaData();

        findViewById(R.id.btnSimpanMassal).setOnClickListener(v -> simpanMassal());
    }

    private void setupFilters() {
        String[] kelasOptions = {"Kelas", "10", "11", "12"};
        ArrayAdapter<String> adapterKelas = new ArrayAdapter<>(this,
                R.layout.spinner_item, kelasOptions);
        adapterKelas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilterKelas.setAdapter(adapterKelas);

        String[] jurusanOptions = {"Jurusan", "RPL", "TKJ", "BC", "DKV", "GD", "ANM"};
        ArrayAdapter<String> adapterJurusan = new ArrayAdapter<>(this,
                R.layout.spinner_item, jurusanOptions);
        adapterJurusan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilterJurusan.setAdapter(adapterJurusan);

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spnFilterKelas.setOnItemSelectedListener(filterListener);
        spnFilterJurusan.setOnItemSelectedListener(filterListener);
    }

    private void loadSiswaData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSiswaList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Siswa siswa = postSnapshot.getValue(Siswa.class);
                    if (siswa != null) {
                        allSiswaList.add(siswa);
                    }
                }
                applyFilter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void applyFilter() {
        String selectedKelas = spnFilterKelas.getSelectedItem().toString();
        String selectedJurusan = spnFilterJurusan.getSelectedItem().toString();

        filteredList.clear();
        for (Siswa s : allSiswaList) {
            boolean matchesKelas = selectedKelas.equals("Kelas") || s.getKelas().equals(selectedKelas);
            boolean matchesJurusan = selectedJurusan.equals("Jurusan") || s.getJurusan().equals(selectedJurusan);
            if (matchesKelas && matchesJurusan) {
                filteredList.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void simpanMassal() {
        Set<String> selectedIds = adapter.getSelectedSiswaIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Pilih minimal satu siswa", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        for (String id : selectedIds) {
            RiwayatKetring record = new RiwayatKetring(null, id, tanggal, RiwayatRepository.HARGA_KETRING, adminEmail);
            repository.addRiwayatKetring(record);
        }

        Toast.makeText(this, "Berhasil mencatat " + selectedIds.size() + " siswa", Toast.LENGTH_SHORT).show();
        finish();
    }
}
