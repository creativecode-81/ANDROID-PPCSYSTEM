package com.francode.college_political_parties;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.francode.college_political_parties.Model.TipoDocumento;
import com.francode.college_political_parties.Utils.Apis;
import com.francode.college_political_parties.Utils.TypeDocService;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUpdate extends AppCompatActivity {

    TextView tv_register_update;
    EditText etName, etNameShort;
    Spinner spnState;
    Button btnSaveUpdate;
    TypeDocService apiService;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encontrar la vista raíz del layout
        rootView = findViewById(R.id.main);
        tv_register_update = findViewById(R.id.tvRegisterUpdate);
        etName = findViewById(R.id.edtName);
        etNameShort = findViewById(R.id.edtNameShort);
        spnState = findViewById(R.id.spnState);
        btnSaveUpdate = findViewById(R.id.btnSaveUpdate);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnState.setAdapter(adapter);

        apiService = Apis.getTypeDocService();

        // Verifica si hay datos recibidos para editar
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_tipodoc")) {
            // Es una edición, rellena los campos con los datos recibidos
            int id = intent.getIntExtra("id_tipodoc", -1);
            String name = intent.getStringExtra("nombre");
            String nameShort = intent.getStringExtra("nombre_corto");
            String state = intent.getStringExtra("estado");

            etName.setText(name);
            etNameShort.setText(nameShort);
            spnState.setSelection(Objects.equals(state, "1") ? 0 : 1); // Selecciona el estado adecuado

            // Cambia el texto del TextView y del botón para indicar que es una actualización
            tv_register_update.setText(R.string.title_update);
            btnSaveUpdate.setText(R.string.update);

            btnSaveUpdate.setOnClickListener(v -> {
                String newName = etName.getText().toString().trim();
                String newNameShort = etNameShort.getText().toString().trim();
                String newState = spnState.getSelectedItem().toString();

                if (!newName.isEmpty() && !newNameShort.isEmpty()) {
                    String newStateValue = newState.equals("Activo") ? "1" : "0";
                    TipoDocumento updatedTypeDoc = new TipoDocumento(newName, newNameShort, newStateValue);
                    updatedTypeDoc.setId_tipodoc(id); // Asigna el ID para que sepa qué registro actualizar
                    updateTypeDoc(updatedTypeDoc);
                } else {
                    showSnackBar(getString(R.string.complete_all_fields));
                }
            });
        } else {
            // Nuevo registro
            btnSaveUpdate.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String name_short = etNameShort.getText().toString().trim();
                String state = spnState.getSelectedItem().toString();

                if (!name.isEmpty() && !name_short.isEmpty()) {
                    String states = state.equals("Activo") ? "1" : "0";
                    TipoDocumento typeDoc = new TipoDocumento(name, name_short, states);
                    saveTypeDoc(typeDoc);
                } else {
                    showSnackBar(getString(R.string.complete_all_fields));
                }
            });
        }
    }

    // Guardar Tipo de Documento
    private void saveTypeDoc(TipoDocumento typeDoc) {
        Call<TipoDocumento> call = apiService.save(typeDoc);
        call.enqueue(new Callback<TipoDocumento>() {
            @Override
            public void onResponse(@NonNull Call<TipoDocumento> call, @NonNull Response<TipoDocumento> response) {
                if (response.isSuccessful()) {
                    showSnackBar(getString(R.string.document_type_saved_successfully));
                    navigateBackToMainActivity();
                } else {
                    handleErrorResponse(response);
                    //showSnackBar(getString(R.string.error_saving));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TipoDocumento> call, @NonNull Throwable t) {
                showSnackBar("Error: " + t.getMessage());
            }
        });
    }

    // Actualizar Tipo de Documento
    private void updateTypeDoc(TipoDocumento typeDoc) {
        Call<TipoDocumento> call = apiService.update(typeDoc);
        call.enqueue(new Callback<TipoDocumento>() {
            @Override
            public void onResponse(@NonNull Call<TipoDocumento> call, @NonNull Response<TipoDocumento> response) {
                if (response.isSuccessful()) {
                    showSnackBar(getString(R.string.document_type_updated_successfully));
                    navigateBackToMainActivity();
                } else {
                    //showSnackBar(getString(R.string.error_updating));
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TipoDocumento> call, @NonNull Throwable t) {
                showSnackBar("Error al actualizar: " + t.getMessage());
            }
        });
    }

    // Manejo de errores en la respuesta del servidor
    private void handleErrorResponse(Response<TipoDocumento> response) {
        if (response.code() == 400) {
            try {
                String errorMessage = Objects.requireNonNull(response.errorBody()).string();
                showSnackBar(errorMessage);
            } catch (IOException e) {
                Log.e("API_ERROR", "Error processing error body", e);
                showSnackBar(getString(R.string.error_saving));
            }
        } else {
            showSnackBar(getString(R.string.error_saving));
        }
    }

    // Navegar de vuelta a la actividad principal después de guardar/actualizar
    private void navigateBackToMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(RegisterUpdate.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2000); // Retraso de 2 segundos
    }

    // Mensaje de alerta
    private void showSnackBar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.snack)).show();
    }
}