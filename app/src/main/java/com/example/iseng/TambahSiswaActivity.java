package com.example.iseng;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TambahSiswaActivity extends AppCompatActivity {

    private EditText etNama;
    private Spinner spnKelas, spnJurusan;
    private Button btnSimpan;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_siswa);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNama = findViewById(R.id.etNama);
        spnKelas = findViewById(R.id.spnKelas);
        spnJurusan = findViewById(R.id.spnJurusan);
        btnSimpan = findViewById(R.id.btnSimpan);
        setupSpinners();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> simpanSiswa());
    }

    private void setupSpinners() {
        String[] daftarKelas = {"10", "11", "12"};
        ArrayAdapter<String> adapterKelas = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarKelas);
        spnKelas.setAdapter(adapterKelas);

        String[] daftarJurusan = {"RPL", "TKJ", "BC", "DKV", "GD", "ANM"};
        ArrayAdapter<String> adapterJurusan = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarJurusan);
        spnJurusan.setAdapter(adapterJurusan);
    }

    private void simpanSiswa() {
        String nama = etNama.getText().toString().trim();
        String kelas = spnKelas.getSelectedItem().toString();
        String jurusan = spnJurusan.getSelectedItem().toString();

        if (TextUtils.isEmpty(nama)) {
            etNama.setError("nama jangan kosong");
            return;
        }

        String idSiswa = mDatabase.child("siswa").push().getKey();
        Map<String, Object> siswaData = new HashMap<>();
        siswaData.put("id", idSiswa);
        siswaData.put("nama", nama);
        siswaData.put("kelas", kelas);
        siswaData.put("jurusan", jurusan);

        if (idSiswa != null) {
            mDatabase.child("siswa").child(idSiswa).setValue(siswaData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(TambahSiswaActivity.this, "Siswa berhasil disimpan", Toast.LENGTH_SHORT).show();
                        etNama.setText(""); // Reset input
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TambahSiswaActivity.this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
