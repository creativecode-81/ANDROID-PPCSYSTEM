package com.francode.college_political_parties;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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

import com.francode.college_political_parties.Model.TipoDocumento;
import com.francode.college_political_parties.Utils.Apis;
import com.francode.college_political_parties.Utils.TypeDocService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    SearchView btnSearch;
    FloatingActionButton btnAdd;
    Button btnViewStudent;
    RecyclerView recyclerView;
    TypeDocAdapter adapter;
    TypeDocService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        btnSearch = findViewById(R.id.btnSearch);
        btnAdd = findViewById(R.id.btnAdd);
        btnViewStudent = findViewById(R.id.btnViewStudent);
        recyclerView = findViewById(R.id.recyclerViewTypeDocs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Agregar divisor entre elementos
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        apiService = Apis.getTypeDocService();
        // Inicializar el adaptador con una lista vacía y configurar el RecyclerView
        adapter = new TypeDocAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Cargar todos los documentos al inicio
        listTypeDocs();

        // Configurar búsqueda
        btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Manejar la búsqueda al enviar la consulta
                searchTypeDoc(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Puedes manejar los cambios de texto aquí si es necesario
                if (newText.isEmpty()) {
                    // Si el texto de búsqueda está vacío, mostrar todos los documentos
                    listTypeDocs();
                } else {
                    // Realizar la búsqueda mientras se escribe
                    searchTypeDoc(newText);
                }
                return false;
            }
        });

        // Configurar el botón para agregar un nuevo tipo de documento
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterUpdate.class);
            startActivity(intent);
        });

        // Configurar el botón para ver los alumnos
        btnViewStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Student.class);
            startActivity(intent);
        });

        // Configurar el ClickListener para traer los datos
        adapter.setOnItemClickListener(position -> {
            TipoDocumento typeDoc = adapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, RegisterUpdate.class);
            intent.putExtra("id_tipodoc", typeDoc.getId_tipodoc());
            intent.putExtra("nombre", typeDoc.getNombre());
            intent.putExtra("nombre_corto", typeDoc.getNombre_corto());
            intent.putExtra("estado", typeDoc.getEstado());
            startActivity(intent);
        });


        // Configurar el long click listener
        adapter.setOnItemLongClickListener(this::showDisableDialog);

    }

    // Listar Tipo de Documentos
    private void listTypeDocs() {
        Call<List<TipoDocumento>> call = apiService.listAll();
        call.enqueue(new Callback<List<TipoDocumento>>() {
            @Override
            public void onResponse(@NonNull Call<List<TipoDocumento>> call, @NonNull Response<List<TipoDocumento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else {
                    showSnackBar(getString(R.string.error_loading_document_types));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TipoDocumento>> call, Throwable t) {
                Log.e("MainActivity", "Error en la llamada: " + t.getMessage());
            }
        });
    }

    // Buscar Tipo de Documentos
    private void searchTypeDoc(String query) {
        Call<List<TipoDocumento>> call;

        // Si la longitud del texto es menor a 4, lo consideramos como nombre_corto
        if (query.length() <= 4) {
            call = apiService.search(null, query); // Búsqueda por nombre_corto
            //showSnackBar("Busqueda exitosa");
        } else {
            call = apiService.search(query, null); // Búsqueda por nombre
            //showSnackBar("Busqueda exitosa");
        }

        call.enqueue(new Callback<List<TipoDocumento>>() {
            @Override
            public void onResponse(@NonNull Call<List<TipoDocumento>> call, @NonNull Response<List<TipoDocumento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TipoDocumento> typeDocs = response.body();
                    adapter = new TypeDocAdapter(typeDocs);
                    recyclerView.setAdapter(adapter);
                } /*else {
                    showSnackBar(getString(R.string.error_loading_document_types));
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<List<TipoDocumento>> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error en la llamada: " + t.getMessage());
                showSnackBar(getString(R.string.error_loading_document_types));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listTypeDocs();
    }

    // Método para mostrar la alerta de habilitar e inhabilitar el tipo de documento
    /*private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.disabled_typeDoc_title))
                .setMessage(getString(R.string.disabled_typeDoc_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    deleteTypeDoc(position);
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }*/

    private void showDisableDialog(int position) {
        TipoDocumento typeDoc = adapter.getItem(position); // Obtén el objeto en la posición correcta

        if (typeDoc != null) {
            String title;
            String message;
            boolean isTypeDocEnabled = typeDoc.getEstado().equals("1");
            if (isTypeDocEnabled) {
                title = getString(R.string.disabled_typeDoc_title); // "Inhabilitar el tipo de documento"
                message = getString(R.string.disabled_typeDoc_message); // "¿Quieres inhabilitar el tipo de documento?"
            } else {
                title = getString(R.string.enabled_typeDoc_title); // "Habilitar el tipo de documento"
                message = getString(R.string.enabled_typeDoc_message); // "¿Quieres habilitar el tipo de documento?"
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                        disableTypeDoc(typeDoc.getId_tipodoc(), isTypeDocEnabled); // Pasa el ID real del documento
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Log.e("MainActivity", "TipoDocumento en la posición " + position + " es nulo");
        }
    }

    // Método para eliminar un tipo de documento
    private void disableTypeDoc(int id, boolean isTypeDocEnabled) {
        Call<TipoDocumento> call = apiService.disable(id);
        call.enqueue(new Callback<TipoDocumento>() {
            @Override
            public void onResponse(@NonNull Call<TipoDocumento> call, @NonNull Response<TipoDocumento> response) {
                if (response.isSuccessful()) {
                    String successMessage = getString(isTypeDocEnabled ? R.string.document_type_disabled_successfully : R.string.document_type_enabled_successfully);
                    showSnackBar(successMessage);
                    listTypeDocs(); // Recargar la lista de documentos
                } else {
                    String error_message = getString(isTypeDocEnabled ? R.string.error_disabling_document_type : R.string.error_enabling_document_type);
                    showSnackBar(error_message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TipoDocumento> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error en la llamada: " + t.getMessage());
            }
        });
    }

    // Mensaje de alerta
    private void showSnackBar(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }
}