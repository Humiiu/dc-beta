package com.example.iseng;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailJurusanAdapter extends RecyclerView.Adapter<DetailJurusanAdapter.ViewHolder> {

    private final List<DetailJurusanActivity.JurusanGroup> list;

    public DetailJurusanAdapter(List<DetailJurusanActivity.JurusanGroup> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_jurusan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailJurusanActivity.JurusanGroup item = list.get(position);
        holder.tvJurusanKelas.setText(item.getDisplayName());
        holder.tvJumlahBelumLunas.setText(item.unpaidStudents + " orang");
        holder.tvTotalInfo.setText("belum lunas dari " + item.totalStudents + " total");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJurusanKelas, tvJumlahBelumLunas, tvTotalInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJurusanKelas = itemView.findViewById(R.id.tvJurusanKelas);
            tvJumlahBelumLunas = itemView.findViewById(R.id.tvJumlahBelumLunas);
            tvTotalInfo = itemView.findViewById(R.id.tvTotalInfo);
        }
    }
}
