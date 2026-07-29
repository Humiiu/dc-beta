package com.example.iseng;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private List<LaporanSiswa> list;

    public LaporanAdapter(List<LaporanSiswa> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaporanSiswa item = list.get(position);
        holder.tvNama.setText(item.getSiswa().getNama());
        holder.tvKelas.setText(item.getSiswa().getKelas() + " - " + item.getSiswa().getJurusan());
        holder.tvSisa.setText("Sisa: " + formatRupiah(item.getSisa()));
        
        holder.tvStatus.setText(item.getStatus().toUpperCase());
        if (item.getStatus().equals("Lunas")) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.pink_accent));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String formatRupiah(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKelas, tvSisa, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaLaporan);
            tvKelas = itemView.findViewById(R.id.tvKelasLaporan);
            tvSisa = itemView.findViewById(R.id.tvSisaLaporan);
            tvStatus = itemView.findViewById(R.id.tvStatusLaporan);
        }
    }
}
