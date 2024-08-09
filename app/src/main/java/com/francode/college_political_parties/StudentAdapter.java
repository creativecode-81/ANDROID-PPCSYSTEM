package com.francode.college_political_parties;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francode.college_political_parties.Model.Alumno;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{
    private final List<Alumno> student_List;

    // Variable para actualizar
    private StudentAdapter.OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(StudentAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Variable para inhabilitar y habilitar
    private StudentAdapter.OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(StudentAdapter.OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public StudentAdapter(List<Alumno> student_List) {
        this.student_List = student_List;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Alumno> newData) {
        student_List.clear();
        student_List.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_student, parent, false);
        return new StudentAdapter.StudentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {
        // Vincula los datos a la vista (rellenar con los valores de cada Alumno)
        Alumno student = student_List.get(position);
        holder.tv_names.setText(String.format("Nombres: %s", student.getNombres()));
        holder.tv_surnames.setText(String.format("Apellidos: %s %s", student.getApellido_paterno(), student.getApellido_materno()));
        holder.tv_type_doc.setText(String.format("Documento: %s", student.getTypeDoc().getNombre_corto()));
        holder.tv_nro_doc.setText(String.format("Nro. Doc: %s", student.getNro_doc()));
        holder.tv_phone.setText(String.format("Teléfono: %s", student.getTelefono()));

        // Convertir el estado
        String state = student.getEstado().equals("1") ? "Activo" : "Inactivo";
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
        return student_List.size();
    }

    public Alumno getItem(int position) {
        return student_List.get(position);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        // Define las vistas que vas a utilizar para mostrar la información
        TextView tv_names, tv_surnames, tv_type_doc, tv_nro_doc, tv_phone, tv_state;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encuentra y asigna las vistas
            tv_names = itemView.findViewById(R.id.tvNames);
            tv_surnames = itemView.findViewById(R.id.tvSurnames);
            tv_type_doc = itemView.findViewById(R.id.tvTypeDoc);
            tv_nro_doc = itemView.findViewById(R.id.tvNroDoc);
            tv_phone = itemView.findViewById(R.id.tvPhone);
            tv_state = itemView.findViewById(R.id.tvStateStudent);

        }
    }
}
