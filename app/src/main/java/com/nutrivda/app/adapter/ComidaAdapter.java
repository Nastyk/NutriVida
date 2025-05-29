package com.nutrivda.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.DetalleComidaActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.model.Comida;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ViewHolder> {

    public interface OnComidaClickListener {
        void onComidaClick(Comida comida);
        void onGuardarClick(Comida comida);
    }

    private final List<Comida> listaComidas;
    private final OnComidaClickListener listener;
    private Comida selectedComida;

    public ComidaAdapter(List<Comida> listaComidas, OnComidaClickListener listener) {
        this.listaComidas = listaComidas;
        this.listener = listener;
    }

    public Comida getSelectedComida() {
        return selectedComida;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comida, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comida comida = listaComidas.get(position);
        Context context = holder.itemView.getContext();
        holder.tvNombre.setText(comida.getDescComida());

        String detalle = comida.getCalorias() + " cal, " +
                comida.getProteinas() + "g prot, " +
                ", " + comida.getGrasas() + "g gras";
        holder.tvDetalle.setText(detalle);

        holder.btnAdd.setOnClickListener(v -> {
            selectedComida = comida;
            notifyDataSetChanged();
            listener.onComidaClick(comida);
            listener.onGuardarClick(comida);
        });

        // Lanzar nueva actividad al pulsar CardView
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleComidaActivity.class);
            intent.putExtra("descComida", comida.getDescComida());
            intent.putExtra("calorias", comida.getCalorias());
            intent.putExtra("proteinas", comida.getProteinas());
            //intent.putExtra("marca", comida.getMarca());
            //intent.putExtra("cantidad", comida.getCantidad());
            context.startActivity(intent);
        });

        // Cambiar ícono si está seleccionada
        if (comida.equals(selectedComida)) {
            holder.btnAdd.setImageResource(R.drawable.ic_check); // icono check
            holder.btnAdd.setAlpha(1.0f);
        } else {
            holder.btnAdd.setImageResource(R.drawable.ic_add); // icono +
            holder.btnAdd.setAlpha(0.4f);
        }
    }

    @Override
    public int getItemCount() {
        return listaComidas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalle;
        ImageButton btnAdd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreComida);
            tvDetalle = itemView.findViewById(R.id.tvDetallesComida);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
