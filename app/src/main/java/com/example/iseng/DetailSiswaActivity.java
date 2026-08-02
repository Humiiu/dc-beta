package com.example.iseng;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailSiswaActivity extends AppCompatActivity {

    private String siswaId;
    private Siswa currentSiswa;

    private TextView tvNama, tvKelasJurusan, tvTotalTagihan, tvTotalDibayar, tvSisaTagihan, tvStatusBayar;
    private CheckBox cbLangganan;
    private RecyclerView rvKetring, rvPembayaran;
    private RiwayatKetringAdapter ketringAdapter;
    private RiwayatPembayaranAdapter pembayaranAdapter;
    
    private List<RiwayatKetring> listKetring = new ArrayList<>();
    private List<RiwayatPembayaran> listPembayaran = new ArrayList<>();
    
    private RiwayatRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_siswa);

        siswaId = getIntent().getStringExtra("siswaId");
        if (siswaId == null) {
            finish();
            return;
        }

        repository = new RiwayatRepository();
        initViews();
        loadSiswaData();
        loadRiwayatData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCatatKetring).setOnClickListener(v -> catatKetringHariIni());
        findViewById(R.id.btnInputBayar).setOnClickListener(v -> showInputBayarDialog());
    }

    private void initViews() {
        tvNama = findViewById(R.id.tvDetailNama);
        tvKelasJurusan = findViewById(R.id.tvDetailKelasJurusan);
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        tvTotalDibayar = findViewById(R.id.tvTotalDibayar);
        tvSisaTagihan = findViewById(R.id.tvSisaTagihan);
        tvStatusBayar = findViewById(R.id.tvStatusBayar);
        cbLangganan = findViewById(R.id.cbLangganan);

        rvKetring = findViewById(R.id.rvRiwayatKetring);
        rvPembayaran = findViewById(R.id.rvRiwayatPembayaran);

        rvKetring.setLayoutManager(new LinearLayoutManager(this));
        ketringAdapter = new RiwayatKetringAdapter(listKetring);
        rvKetring.setAdapter(ketringAdapter);

        rvPembayaran.setLayoutManager(new LinearLayoutManager(this));
        pembayaranAdapter = new RiwayatPembayaranAdapter(listPembayaran);
        rvPembayaran.setAdapter(pembayaranAdapter);

        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback ketringCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                RiwayatKetring record = listKetring.get(position);
                showDeleteConfirmation("ketring", record.getId(), record.getTanggal(), position);
            }
        };
        new ItemTouchHelper(ketringCallback).attachToRecyclerView(rvKetring);

        ItemTouchHelper.SimpleCallback pembayaranCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                RiwayatPembayaran record = listPembayaran.get(position);
                showDeleteConfirmation("pembayaran", record.getId(), record.getTanggal(), position);
            }
        };
        new ItemTouchHelper(pembayaranCallback).attachToRecyclerView(rvPembayaran);
    }

    private void showDeleteConfirmation(String type, String id, String date, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Record");
        String message = type.equals("ketring") ? 
                "Hapus catatan ketring tanggal " + date + "?" :
                "Hapus catatan pembayaran tanggal " + date + "?";
        builder.setMessage(message);
        
        builder.setPositiveButton("Hapus", (dialog, which) -> {
            if (type.equals("ketring")) {
                repository.deleteRiwayatKetring(id);
            } else {
                repository.deleteRiwayatPembayaran(id);
            }
            Toast.makeText(this, "Record berhasil dihapus", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Batal", (dialog, which) -> {
            if (type.equals("ketring")) {
                ketringAdapter.notifyItemChanged(position);
            } else {
                pembayaranAdapter.notifyItemChanged(position);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void loadSiswaData() {
        FirebaseDatabase.getInstance().getReference("siswa").child(siswaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentSiswa = snapshot.getValue(Siswa.class);
                if (currentSiswa != null) {
                    tvNama.setText(currentSiswa.getNama());
                    tvKelasJurusan.setText(currentSiswa.getKelas() + " - " + currentSiswa.getJurusan());
                    
                    cbLangganan.setOnCheckedChangeListener(null);
                    cbLangganan.setChecked(currentSiswa.isLangganan());
                    cbLangganan.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        FirebaseDatabase.getInstance().getReference("siswa").child(siswaId)
                                .child("langganan").setValue(isChecked);
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadRiwayatData() {
        repository.getRiwayatKetringBySiswa(siswaId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listKetring.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listKetring.add(ds.getValue(RiwayatKetring.class));
                }
                Collections.reverse(listKetring);
                ketringAdapter.notifyDataSetChanged();
                updateSummary();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        repository.getRiwayatPembayaranBySiswa(siswaId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPembayaran.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listPembayaran.add(ds.getValue(RiwayatPembayaran.class));
                }
                Collections.reverse(listPembayaran);
                pembayaranAdapter.notifyDataSetChanged();
                updateSummary();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateSummary() {
        long totalTagihan = RiwayatRepository.calculateTotalTagihan(listKetring);
        long totalDibayar = RiwayatRepository.calculateTotalDibayar(listPembayaran);
        long sisa = totalTagihan - totalDibayar;

        tvTotalTagihan.setText(formatRupiah(totalTagihan));
        tvTotalDibayar.setText(formatRupiah(totalDibayar));
        tvSisaTagihan.setText(formatRupiah(sisa));

        if (sisa <= 0 && totalTagihan > 0) {
            tvStatusBayar.setText("LUNAS");
            tvStatusBayar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatusBayar.setText("BELUM LUNAS");
            tvStatusBayar.setBackgroundColor(getResources().getColor(R.color.pink_accent));
        }
    }

    private void catatKetringHariIni() {
        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        
        RiwayatKetring record = new RiwayatKetring(null, siswaId, tanggal, RiwayatRepository.HARGA_KETRING, adminEmail);
        repository.addRiwayatKetring(record);
        Toast.makeText(this, "berhasil nyatet ketring hari ini", Toast.LENGTH_SHORT).show();
    }

    private void showInputBayarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Pembayaran");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Nominal Pembayaran");
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                long nominal = Long.parseLong(text);
                savePembayaran(nominal);
            }
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void savePembayaran(long nominal) {
        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        RiwayatPembayaran record = new RiwayatPembayaran(null, siswaId, tanggal, nominal, adminEmail);
        repository.addRiwayatPembayaran(record);
        Toast.makeText(this, "berhasil nyatet pembayaran", Toast.LENGTH_SHORT).show();
    }

    private String formatRupiah(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }
}
