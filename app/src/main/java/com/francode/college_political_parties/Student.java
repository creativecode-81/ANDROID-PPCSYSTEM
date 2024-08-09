package com.francode.college_political_parties;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.francode.college_political_parties.Model.Alumno;
import com.francode.college_political_parties.Utils.Apis;
import com.francode.college_political_parties.Utils.StudentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Student extends AppCompatActivity {

    SearchView btnSearch;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    StudentAdapter adapter;
    StudentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        btnSearch = findViewById(R.id.btnSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Agregar divisor entre elementos
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        apiService = Apis.getStudent();
        // Inicializar el adaptador con una lista vacía y configurar el RecyclerView
        adapter = new StudentAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Cargar todos los alumnos
        listStudents();

        // Configurar búsqueda
        btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Manejar la búsqueda al enviar la consulta
                searchStudent(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Puedes manejar los cambios de texto aquí si es necesario
                if (newText.isEmpty()) {
                    // Si el texto de búsqueda está vacío, mostrar todos los alumnos
                    listStudents();
                } else {
                    // Realizar la búsqueda mientras se escribe
                    searchStudent(newText);
                }
                return false;
            }
        });

        // Configurar el botón para agregar un nuevo tipo de documento
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Student.this, RegisterUpdateStudent.class);
            startActivity(intent);
        });

        // Configurar el ClickListener para traer los datos
        adapter.setOnItemClickListener(position -> {
            Alumno student = adapter.getItem(position);
            Intent intent = new Intent(Student.this, RegisterUpdateStudent.class);
            intent.putExtra("id_alumno", student.getId_alumno());
            intent.putExtra("nombres", student.getNombres());
            intent.putExtra("apellido_paterno", student.getApellido_paterno());
            intent.putExtra("apellido_materno", student.getApellido_materno());
            intent.putExtra("id_tipodoc", student.getTypeDoc().getId_tipodoc());
            intent.putExtra("nro_doc", student.getNro_doc());
            intent.putExtra("telefono", student.getTelefono());
            intent.putExtra("estado", student.getEstado());
            startActivity(intent);
        });


        // Configurar el long click listener
        adapter.setOnItemLongClickListener(this::showDisableDialog);
    }

    // Listar Alumnos
    private void listStudents() {
        Call<List<Alumno>> call = apiService.listAll();
        call.enqueue(new Callback<List<Alumno>>() {
            @Override
            public void onResponse(@NonNull Call<List<Alumno>> call, @NonNull Response<List<Alumno>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else {
                    showSnackBar(getString(R.string.error_loading_students));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Alumno>> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error en la llamada: " + t.getMessage());
            }
        });
    }

    // Método para buscar alumnos por nombre, apellidos, número de documento o teléfono
    private void searchStudent(String query) {
        // Elimina espacios en blanco al inicio y al final de la consulta
        query = query.trim();

        // Define los valores predeterminados para los parámetros de búsqueda
        String nombres = null;
        String apellidoPaterno = null;
        String apellidoMaterno = null;
        String nroDoc = null;
        String telefono = null;

        // Verifica si el query es un número de teléfono con prefijo internacional
        if (query.startsWith("+") && query.substring(1).matches("\\d+")) {
            telefono = query;
        } else if (query.matches("\\d+")) {
            // Si el query es solo numérico, intenta buscarlo como nro_doc o un teléfono sin prefijo
            if (query.length() <= 8) { // Ajusta según la longitud esperada para un nro_doc
                nroDoc = query;
            } else {
                telefono = query;
            }
        } else {
            // Si es alfabético, busca en los campos nombres, apellido paterno o materno
            if (query.contains(" ")) {
                // Si contiene un espacio, asume que es un nombre completo (nombres + apellido(s))
                String[] parts = query.split(" ");
                if (parts.length == 2) {
                    nombres = parts[0];
                    apellidoPaterno = parts[1];
                } else if (parts.length == 3) {
                    nombres = parts[0];
                    apellidoPaterno = parts[1];
                    apellidoMaterno = parts[2];
                }
            } else {
                // Si no contiene espacio, puede ser un nombre o apellido único
                nombres = query;
            }
        }

        // Log de parámetros enviados
        Log.d("SearchParams", "Nombres: " + nombres + ", Apellido Paterno: " + apellidoPaterno +
                ", Apellido Materno: " + apellidoMaterno + ", Nro Doc: " + nroDoc +
                ", Telefono: " + telefono);

        // Llama a la API con la consulta proporcionada
        Call<List<Alumno>> call = apiService.search(nombres, apellidoPaterno, apellidoMaterno, nroDoc, telefono);
        call.enqueue(new Callback<List<Alumno>>() {
            @Override
            public void onResponse(@NonNull Call<List<Alumno>> call, @NonNull Response<List<Alumno>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualiza el adaptador con los resultados de la búsqueda
                    adapter.updateData(response.body());
                } else {
                    handleErrorResponse(response);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Alumno>> call, @NonNull Throwable t) {
                Log.e("StudentActivity", "Error en la llamada de búsqueda: " + t.getMessage());
            }
        });
    }

    // Manejo de errores en la respuesta del servidor
    private void handleErrorResponse(Response<List<Alumno>> response) {
        if (response.code() == 404) {
            try {
                String errorMessage = Objects.requireNonNull(response.errorBody()).string();
                showSnackBar(errorMessage);
                adapter.updateData(new ArrayList<>()); // Limpia la lista en la interfaz
            } catch (IOException e) {
                Log.e("API_ERROR", "Error processing error body", e);
                showSnackBar(getString(R.string.error_loading_students));
            }
        } else {
            showSnackBar(getString(R.string.error_loading_students));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listStudents();
    }

    // Método para mostrar la alerta de habilitar e inhabilitar un alumno
    private void showDisableDialog(int position) {
        Alumno student = adapter.getItem(position); // Obtén el objeto en la posición correcta

        if (student != null) {
            String title;
            String message;
            boolean isStudentEnabled = student.getEstado().equals("1");
            if (isStudentEnabled) {
                title = getString(R.string.disabled_student_title); // "Inhabilitar el alumno"
                message = getString(R.string.disabled_student_message); // "¿Quieres inhabilitar el alumno?"
            } else {
                title = getString(R.string.enabled_student_title); // "Habilitar el alumno"
                message = getString(R.string.enabled_student_message); // "¿Quieres habilitar el alumno?"
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                        disableTypeDoc(student.getId_alumno(), isStudentEnabled); // Pasa el ID real del alumno
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Log.e("MainActivity", "Alumno en la posición " + position + " es nulo");
        }
    }

    // Método para habilitar e inhabilitar un alumno
    private void disableTypeDoc(int id, boolean isStudentEnabled) {
        Call<Alumno> call = apiService.disable(id);
        call.enqueue(new Callback<Alumno>() {
            @Override
            public void onResponse(@NonNull Call<Alumno> call, @NonNull Response<Alumno> response) {
                if (response.isSuccessful()) {
                    String successMessage = getString(isStudentEnabled ? R.string.student_disabled_successfully : R.string.student_enabled_successfully);
                    showSnackBar(successMessage);
                    listStudents(); // Recargar la lista de alumnos
                } else {
                    String error_message = getString(isStudentEnabled ? R.string.error_disabling_student : R.string.error_enabling_student);
                    showSnackBar(error_message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Alumno> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error en la llamada: " + t.getMessage());
            }
        });
    }

    // Mensaje de alerta
    private void showSnackBar(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }
}