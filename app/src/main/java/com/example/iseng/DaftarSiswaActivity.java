package com.example.iseng;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaftarSiswaActivity extends AppCompatActivity {

    private Spinner spnFilterKelas, spnFilterJurusan;
    private EditText etSearch;
    private RecyclerView rvSiswa;
    private SiswaAdapter adapter;
    private List<Siswa> allSiswaList = new ArrayList<>();
    private List<Siswa> filteredList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_siswa);

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference("siswa");
        } catch (Exception e) {
            Toast.makeText(this, "firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        spnFilterKelas = findViewById(R.id.spnFilterKelas);
        spnFilterJurusan = findViewById(R.id.spnFilterJurusan);
        etSearch = findViewById(R.id.etSearchSiswa);
        rvSiswa = findViewById(R.id.rvSiswa);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if (rvSiswa != null) {
            rvSiswa.setLayoutManager(new LinearLayoutManager(this));
            adapter = new SiswaAdapter(filteredList);
            rvSiswa.setAdapter(adapter);
        }

        setupFilters();
        loadSiswaData();
        setupSearch();

        findViewById(R.id.btnTambahSiswa).setOnClickListener(v -> 
            startActivity(new Intent(this, TambahSiswaActivity.class))
        );
    }

    private void setupSearch() {
        if (etSearch == null) return;
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

    private void setupFilters() {
        String[] kelasOptions = {"Kelas", "10", "11" , "12"};
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
        if (mDatabase == null) return;
        
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSiswaList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Siswa siswa = postSnapshot.getValue(Siswa.class);
                        if (siswa != null) {
                            allSiswaList.add(siswa);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Sort A-Z
                Collections.sort(allSiswaList, (s1, s2) -> s1.getNama().compareToIgnoreCase(s2.getNama()));
                applyFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DaftarSiswaActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        if (spnFilterKelas == null || spnFilterJurusan == null) return;

        String selectedKelas = spnFilterKelas.getSelectedItem().toString();
        String selectedJurusan = spnFilterJurusan.getSelectedItem().toString();
        String query = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";

        filteredList.clear();
        for (Siswa s : allSiswaList) {
            boolean matchesKelas = selectedKelas.equals("Kelas") || s.getKelas().equals(selectedKelas);
            boolean matchesJurusan = selectedJurusan.equals("Jurusan") || s.getJurusan().equals(selectedJurusan);
            boolean matchesSearch = s.getNama().toLowerCase().contains(query);

            if (matchesKelas && matchesJurusan && matchesSearch) {
                filteredList.add(s);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
