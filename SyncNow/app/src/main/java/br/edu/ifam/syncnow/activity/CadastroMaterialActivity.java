package br.edu.ifam.syncnow.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.edu.ifam.syncnow.R;
import br.edu.ifam.syncnow.entity.MaterialEstudo;
import br.edu.ifam.syncnow.repository.MaterialDAO;

public class CadastroMaterialActivity extends AppCompatActivity {

    private EditText editTitulo, editDescricao;
    private Spinner spinnerDisciplina;
    private RadioGroup radioGroupTags;
    private String selectedOption;

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private TextView textViewArquivo;
    private String arquivoUri;

    private MaterialDAO materialDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
        materialDAO = new MaterialDAO(this);
        editTitulo = findViewById(R.id.edit_titulo);
        editDescricao = findViewById(R.id.edit_descricao);
        radioGroupTags = findViewById(R.id.tagsGroup);
        spinnerDisciplina = findViewById(R.id.spinner_disciplina);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.disciplina_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);

        spinnerDisciplina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ler o item selecionado
                selectedOption = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedOption = "Nenhuma opção selecionada"; // Ou você pode definir um valor padrão
            }
        });

        textViewArquivo = findViewById(R.id.textview_arquivo);
        Button buttonSelecionarArquivo = findViewById(R.id.button_selecionar_arquivo);

        buttonSelecionarArquivo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*"); // Garante que o arquivo seja aberto
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData(); // Obtém a URI do arquivo selecionado

                // Caminho do arquivo onde o arquivo será salvo no armazenamento interno
                String fileName = getFileName(uri);
                File destinationFile = new File(getFilesDir(), fileName); // Diretório interno

                try {
                    // Copiar o arquivo para o diretório do aplicativo
                    copyFile(uri, destinationFile);

                    // Exibir o caminho do arquivo salvo
                    arquivoUri = destinationFile.getAbsolutePath();

                    Toast.makeText(this, "Arquivo salvo em: " + arquivoUri, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao salvar o arquivo", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Método para copiar o arquivo
    private void copyFile(Uri sourceUri, File destinationFile) throws IOException {
        try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    // Método para obter o nome do arquivo a partir da URI
    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                textViewArquivo.setText(fileName);
                textViewArquivo.setVisibility(View.VISIBLE);
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName != null ? fileName : "default_image.jpg";
    }
    public void saveButtonOnClick(View view){
        // Obter os dados dos EditTexts
        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        String disciplina = selectedOption;

        int selectedItemPosition = spinnerDisciplina.getSelectedItemPosition();
        disciplina = spinnerDisciplina.getItemAtPosition(selectedItemPosition) != null ? spinnerDisciplina.getItemAtPosition(selectedItemPosition).toString() : "Nenhuma opção selecionada";

        int selectedId = radioGroupTags.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String tag = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "Nenhuma opção selecionada";

        MaterialEstudo material = new MaterialEstudo(titulo, descricao, disciplina, tag);

        // Salvar o arquivo junto aos outros dados, se um arquivo foi selecionado
        if (arquivoUri != null) {
            material.setArquivoUri(arquivoUri); // Supondo que você tenha um campo para armazenar a URI do arquivo
        }

        materialDAO.insert(material);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}