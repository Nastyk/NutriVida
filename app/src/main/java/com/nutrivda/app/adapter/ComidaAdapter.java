package com.nutrivda.app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.R;
import com.nutrivda.app.model.Comida;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder> {
    private List<Comida> listaComidas;
    private int selectedPosition = -1; // Para saber qué elemento está seleccionado
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Comida comida);
    }

    public ComidaAdapter(List<Comida> listaComidas, OnItemClickListener listener) {
        this.listaComidas = listaComidas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comida, parent, false);
        return new ComidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComidaViewHolder holder, int position) {
        Comida comida = listaComidas.get(position);
        holder.tvNombre.setText(comida.getDescComida());
        holder.tvCalorias.setText(comida.getCalorias() + " kcal");

        // Cambia el color si está seleccionado
        holder.itemView.setBackgroundColor(
                selectedPosition == holder.getAdapterPosition() ?
                        holder.itemView.getContext().getResources().getColor(R.color.light_gray) :
                        holder.itemView.getContext().getResources().getColor(android.R.color.white)
        );

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition(); // Usar getAdapterPosition() en lugar de position
            notifyDataSetChanged(); // Refresca la lista para cambiar la selección
            listener.onItemClick(comida);
        });
    }

    @Override
    public int getItemCount() {
        return listaComidas.size();
    }

    public Comida getSelectedComida() {
        return selectedPosition != -1 ? listaComidas.get(selectedPosition) : null;
    }

    public static class ComidaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCalorias;

        public ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCalorias = itemView.findViewById(R.id.tvCalorias);
        }
    }
}
