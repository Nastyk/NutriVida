package com.nutrivda.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.activity.DetalleComidaActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.model.Comida;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ViewHolder> {

    public interface OnComidaClickListener {
        void onComidaClick(Comida comida);
        void onGuardarClick(Comida comida);
        void onEliminarClick(Comida comida);
    }

    private final List<Comida> listaComidas;
    private final OnComidaClickListener listener;
    private Comida selectedComida;
    private boolean mostrarBoton = true;
    private boolean esEdicion = false;
    private final ActivityResultLauncher<Intent> launcher;

    public ComidaAdapter(List<Comida> listaComidas, OnComidaClickListener listener, @Nullable ActivityResultLauncher<Intent> launcher) {
        this.listaComidas = listaComidas;
        this.listener = listener;
        this.launcher = launcher;
    }

    public Comida getSelectedComida() {
        return selectedComida;
    }

    public void setMostrarBoton(boolean mostrar) {
        this.mostrarBoton = mostrar;
        notifyDataSetChanged(); // refresca la lista
    }

    public void setEsEdicion(boolean esEdicion) {
        this.esEdicion = esEdicion;
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

        holder.btnAdd.setVisibility(mostrarBoton ? View.VISIBLE : View.GONE);

        holder.btnAdd.setOnClickListener(v -> {
            selectedComida = comida;
            notifyDataSetChanged();
            listener.onComidaClick(comida);
            listener.onGuardarClick(comida);
        });

        // Lanzar nueva actividad al pulsar CardView
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleComidaActivity.class);
            intent.putExtra("idAlimento", comida.getId());
            intent.putExtra("esEdicion", esEdicion);
            if (launcher != null) {
                launcher.launch(intent); // desde Fragment
            } else {
                context.startActivity(intent); // desde Activity
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (esEdicion) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.menu_comida_item, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_eliminar) {
                        listener.onEliminarClick(comida);
                        return true;
                    }
                    return false;
                });

                popup.show();
                return true;
            }
            return false;
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
