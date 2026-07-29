package com.example.iseng;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SiswaMassalAdapter extends RecyclerView.Adapter<SiswaMassalAdapter.ViewHolder> {

    private List<Siswa> list;
    private Set<String> selectedSiswaIds = new HashSet<>();

    public SiswaMassalAdapter(List<Siswa> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_siswa_massal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Siswa siswa = list.get(position);
        holder.tvNama.setText(siswa.getNama());
        holder.tvKelasJurusan.setText(siswa.getKelas() + " - " + siswa.getJurusan());
        
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedSiswaIds.contains(siswa.getId()));
        
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSiswaIds.add(siswa.getId());
            } else {
                selectedSiswaIds.remove(siswa.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Set<String> getSelectedSiswaIds() {
        return selectedSiswaIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKelasJurusan;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaSiswa);
            tvKelasJurusan = itemView.findViewById(R.id.tvKelasJurusan);
            checkBox = itemView.findViewById(R.id.cbSelected);
        }
    }
}
