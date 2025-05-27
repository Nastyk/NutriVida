package com.nutrivda.app.test;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.R;
import java.util.List;

public class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ViewHolder> {

    private final List<ResultadoTest> lista;

    public ResultadoAdapter(List<ResultadoTest> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ResultadoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoAdapter.ViewHolder holder, int position) {
        ResultadoTest item = lista.get(position);
        holder.tvFecha.setText("📅 " + item.getFecha());
        holder.tvTipo.setText("🧠 Tipo: " + item.getTipo());
        holder.tvResultado.setText("✅ Resultado: " + item.getResultado());
        holder.tvResumen.setText("📝 Resumen:\n" + item.getResumen());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvTipo, tvResultado, tvResumen;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvResultado = itemView.findViewById(R.id.tvResultado);
            tvResumen = itemView.findViewById(R.id.tvResumen);
        }
    }
}