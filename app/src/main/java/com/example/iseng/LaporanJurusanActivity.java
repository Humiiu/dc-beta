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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LaporanJurusanActivity extends AppCompatActivity {

    private RecyclerView rvJurusan;
    private List<String> jurusans = Arrays.asList("RPL", "TKJ", "BC", "DKV", "GD", "ANM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_jurusan);

        rvJurusan = findViewById(R.id.rvJurusan);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Collections.sort(jurusans);

        rvJurusan.setLayoutManager(new LinearLayoutManager(this));
        rvJurusan.setAdapter(new JurusanAdapter(jurusans, jurusan -> {
            Intent intent = new Intent(this, LaporanTanggalActivity.class);
            intent.putExtra("jurusan", jurusan);
            startActivity(intent);
        }));
    }

    private static class JurusanAdapter extends RecyclerView.Adapter<JurusanAdapter.ViewHolder> {
        private List<String> list;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String jurusan);
        }

        public JurusanAdapter(List<String> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_jurusan, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String jurusan = list.get(position);
            holder.tvNama.setText(jurusan);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(jurusan));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvJurusanKelas);
            }
        }
    }
}
