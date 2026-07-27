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

public class RiwayatKetringAdapter extends RecyclerView.Adapter<RiwayatKetringAdapter.ViewHolder> {

    private List<RiwayatKetring> list;

    public RiwayatKetringAdapter(List<RiwayatKetring> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatKetring item = list.get(position);
        holder.tvTanggal.setText(item.getTanggal());
        holder.tvValue.setText(formatRupiah(item.getHarga()));
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
        TextView tvTanggal, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}
