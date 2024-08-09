package com.francode.college_political_parties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.francode.college_political_parties.Model.Alumno;
import com.francode.college_political_parties.Model.TipoDocumento;
import com.francode.college_political_parties.Utils.Apis;
import com.francode.college_political_parties.Utils.StudentService;
import com.francode.college_political_parties.Utils.TypeDocService;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUpdateStudent extends AppCompatActivity {

    TextView tv_register_update;
    EditText et_names, et_paternal_surname, et_maternal_surname, et_nro_doc, et_phone;
    Spinner spn_type_doc, spn_state;
    Button btn_save_update_student;
    StudentService apiService;
    TypeDocService apiServTypeDoc;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_update_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinner();
        loadTypeDocs();
        checkForEditData();
    }

    // Inicializar las variables
    private void initViews() {
        // Encontrar la vista raíz del layout
        rootView = findViewById(R.id.main);
        tv_register_update = findViewById(R.id.tvSaveUpdateStudent);
        et_names = findViewById(R.id.edtNames);
        et_paternal_surname = findViewById(R.id.edtPaternalSurname);
        et_maternal_surname = findViewById(R.id.edtMaternalSurname);
        spn_type_doc = findViewById(R.id.spnTypeDoc);
        et_nro_doc = findViewById(R.id.edtNroDoc);
        et_phone = findViewById(R.id.edtPhone);
        spn_state = findViewById(R.id.spnStateStudent);
        btn_save_update_student = findViewById(R.id.btnSaveUpdateStudent);

        apiServTypeDoc = Apis.getTypeDocService();
        apiService = Apis.getStudent();
    }

    // Configurar Spinner
    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_state.setAdapter(adapter);
    }

    // Listar los tipos de documentos
    private void loadTypeDocs() {
        Call<List<TipoDocumento>> call = apiServTypeDoc.listAll();
        call.enqueue(new Callback<List<TipoDocumento>>() {
            @Override
            public void onResponse(@NonNull Call<List<TipoDocumento>> call, @NonNull Response<List<TipoDocumento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TipoDocumento> type_docs = response.body();

                    ArrayAdapter<TipoDocumento> adapter = new ArrayAdapter<>(RegisterUpdateStudent.this,
                            android.R.layout.simple_spinner_item, type_docs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_type_doc.setAdapter(adapter);

                    // Si estás en modo edición, selecciona el tipo de documento correcto en el Spinner
                    Intent intent = getIntent();
                    if (intent != null && intent.hasExtra("id_alumno")) {
                        int id_tipodoc = intent.getIntExtra("id_tipodoc", -1);
                        if (id_tipodoc != -1) {
                            // Busca y selecciona el tipo de documento en el Spinner
                            for (int i = 0; i < type_docs.size(); i++) {
                                if (type_docs.get(i).getId_tipodoc() == id_tipodoc) {
                                    spn_type_doc.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    showSnackBar(getString(R.string.error_loading_document_types));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TipoDocumento>> call, @NonNull Throwable t) {
                showSnackBar("Error: " + t.getMessage());
            }
        });
    }

    // Verificar si hay datos para actualizar
    private void checkForEditData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_alumno")) {
            populateFieldsForEdit(intent);
            setupUpdateButton(intent.getIntExtra("id_alumno", -1));
        } else {
            setupSaveButton();
        }
    }

    // Es una edición, rellena los campos con los datos recibidos
    private void populateFieldsForEdit(Intent intent) {
        et_names.setText(intent.getStringExtra("nombres"));
        et_paternal_surname.setText(intent.getStringExtra("apellido_paterno"));
        et_maternal_surname.setText(intent.getStringExtra("apellido_materno"));
        et_nro_doc.setText(intent.getStringExtra("nro_doc"));
        et_phone.setText(intent.getStringExtra("telefono"));
        spn_state.setSelection(Objects.equals(intent.getStringExtra("estado"), "1") ? 0 : 1); // Selecciona el estado adecuado
        // Cambia el texto del TextView y del botón para indicar que es una actualización
        tv_register_update.setText(R.string.title_update_student);
        btn_save_update_student.setText(R.string.update);
    }

    // Método para manejar el evento de clic del botón actualizar
    private void setupUpdateButton(final int studentId) {
        btn_save_update_student.setOnClickListener(v -> {
            if (validateAndFocusFields()) {
                Alumno updatedStudent = collectStudentData();
                updatedStudent.setId_alumno(studentId);
                saveOrUpdateStudent(updatedStudent, true);
            }
        });
    }

    // Método para manejar el evento de clic del botón registrar
    private void setupSaveButton() {
        btn_save_update_student.setOnClickListener(v -> {
            if (validateAndFocusFields()) {
                Alumno newStudent = collectStudentData();
                saveOrUpdateStudent(newStudent, false);
            }
        });
    }

    // Método que recoge los datos del formulario en un solo lugar
    private Alumno collectStudentData() {
        String name = et_names.getText().toString().trim();
        String paternalSurname = et_paternal_surname.getText().toString().trim();
        String maternalSurname = et_maternal_surname.getText().toString().trim();
        TipoDocumento typeDoc = (TipoDocumento) spn_type_doc.getSelectedItem();
        String nroDoc = et_nro_doc.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String state = spn_state.getSelectedItem().toString().equals("Activo") ? "1" : "0";

        return new Alumno(name, paternalSurname, maternalSurname, typeDoc, nroDoc, phone, state);
    }

    // Método para validar todos los campos
    private boolean validateAndFocusFields() {
        return validateField(et_names, getString(R.string.error_name))
                && validateField(et_paternal_surname, getString(R.string.error_paternal_surname))
                && validateField(et_maternal_surname, getString(R.string.error_maternal_surname))
                && validateDocNumber(et_nro_doc)
                && validatePhoneNumber(et_phone);
    }

    // Validación de campos vacíos
    private boolean validateField(EditText field, String emptyErrorMessage) {
        String text = field.getText().toString().trim();
        if (text.isEmpty()) {
            return showErrorAndFocus(field, emptyErrorMessage);
        }
        return true;
    }

    // Método para validar el número de documento
    private boolean validateDocNumber(EditText field) {
        String docNumber = field.getText().toString().trim();
        if (docNumber.isEmpty()) {
            return showErrorAndFocus(field, getString(R.string.error_doc_number));
        } else if (!docNumber.matches("^\\d{8,11}$")) {
            showAlert(getString(R.string.invalid_doc_number), getString(R.string.error_doc_number_format));
            return showErrorAndFocus(field, getString(R.string.error_doc_number_format));
        }
        return true;
    }

    // Método para validar el número de teléfono
    private boolean validatePhoneNumber(EditText field) {
        String phoneNumber = field.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return showErrorAndFocus(field, getString(R.string.error_phone_number));
        } else if (!phoneNumber.matches("^(\\+(51))? ?\\d{9}$")) {
            showAlert(getString(R.string.invalid_phone_number), getString(R.string.error_phone_number_format));
            return showErrorAndFocus(field, getString(R.string.error_phone_number_format));
        }
        return true;
    }

    // Método para mostrar un mensaje de error y enfocar el campo que no pasó la validación.
    private boolean showErrorAndFocus(EditText field, String errorMessage) {
        focusAndShowKeyboard(field);
        showSnackBar(errorMessage);
        return false;
    }

    // Método para mostrar una alerta
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Método generalizado para guardar o actualizar Alumnos
    private void saveOrUpdateStudent(Alumno student, boolean isUpdate) {
        Call<Alumno> call = isUpdate ? apiService.update(student) : apiService.save(student);
        call.enqueue(new Callback<Alumno>() {
            @Override
            public void onResponse(@NonNull Call<Alumno> call, @NonNull Response<Alumno> response) {
                if (response.isSuccessful()) {
                    String successMessage = getString(isUpdate ? R.string.student_updated_successfully : R.string.student_saved_successfully);
                    showSnackBar(successMessage);
                    navigateBackToMainActivity();
                } else {
                    handleErrorResponse(response, isUpdate);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Alumno> call, @NonNull Throwable t) {
                String errorMessage = getString(isUpdate ? R.string.error_updating_student : R.string.error_saving_student) + ": " + t.getMessage();
                showSnackBar(errorMessage);
            }
        });
    }

    // Manejo de errores en la respuesta del servidor
    private void handleErrorResponse(Response<Alumno> response, boolean isUpdate) {
        String errorMessage = null;
        if (response.code() == 400) { // 400 Bad Request
            try {
                errorMessage = Objects.requireNonNull(response.errorBody()).string();
            } catch (IOException e) {
                Log.e("API_ERROR", "Error processing error body", e);
            }
        }
        if (errorMessage == null) {
            errorMessage = getString(isUpdate ? R.string.error_updating_student : R.string.error_saving_student);
        }

        showSnackBar(errorMessage);
    }

    // Navegar de vuelta a la actividad principal después de guardar/actualizar
    private void navigateBackToMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(RegisterUpdateStudent.this, Student.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2000); // Retraso de 2 segundos
    }

    // Función para que el teclado se muestre cuando sea necesario.
    private void focusAndShowKeyboard(EditText editText) {
        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    // Mensaje de alerta
    private void showSnackBar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.snack)).show();
    }
}