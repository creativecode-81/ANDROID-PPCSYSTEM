package com.francode.college_political_parties;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francode.college_political_parties.Model.TipoDocumento;

import java.util.List;

public class TypeDocAdapter extends RecyclerView.Adapter<TypeDocAdapter.TypeDocViewHolder> {
    private final List<TipoDocumento> tipoDoc_List;

    // Variable para actualizar
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Variable para inhabilitar y habilitar
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public TypeDocAdapter(List<TipoDocumento> tipoDoc_List) {
        this.tipoDoc_List = tipoDoc_List;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<TipoDocumento> newData) {
        tipoDoc_List.clear();
        tipoDoc_List.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TypeDocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_typedoc, parent, false);
        return new TypeDocViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TypeDocViewHolder holder, int position) {
        // Vincula los datos a la vista (rellenar con los valores de cada Tipo de Documento)
        TipoDocumento typeDocs = tipoDoc_List.get(position);
        holder.tv_name.setText(String.format("Nombre: %s", typeDocs.getNombre()));
        holder.tv_shortName.setText(String.format("Nombre corto: %s", typeDocs.getNombre_corto()));
        // Convertir el estado
        String state = typeDocs.getEstado().equals("1") ? "Activo" : "Inactivo";
        holder.tv_state.setText(String.format("Estado: %s", state));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tipoDoc_List.size();
    }

    public TipoDocumento getItem(int position) {
        return tipoDoc_List.get(position);
    }

    public static class TypeDocViewHolder extends RecyclerView.ViewHolder {
        // Define las vistas que vas a utilizar para mostrar la información
        TextView tv_name, tv_shortName, tv_state;

        public TypeDocViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encuentra y asigna las vistas (TextViews, etc.)
            tv_name = itemView.findViewById(R.id.tvName);
            tv_shortName = itemView.findViewById(R.id.tvShortName);
            tv_state = itemView.findViewById(R.id.tvState);

        }
    }
}
