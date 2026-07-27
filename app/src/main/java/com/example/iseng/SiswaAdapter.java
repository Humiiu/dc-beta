package com.example.iseng;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder> {

    private List<Siswa> siswaList;

    public SiswaAdapter(List<Siswa> siswaList) {
        this.siswaList = siswaList;
    }

    @NonNull
    @Override
    public SiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_siswa, parent, false);
        return new SiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiswaViewHolder holder, int position) {
        Siswa siswa = siswaList.get(position);
        if (siswa != null) {
            holder.tvNama.setText(siswa.getNama());
            holder.tvKelas.setText(siswa.getKelas() + " - " + siswa.getJurusan());
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DetailSiswaActivity.class);
                intent.putExtra("siswaId", siswa.getId());
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return (siswaList != null) ? siswaList.size() : 0;
    }

    public static class SiswaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKelas;

        public SiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvKelas = itemView.findViewById(R.id.tvKelas);
        }
    }
}
