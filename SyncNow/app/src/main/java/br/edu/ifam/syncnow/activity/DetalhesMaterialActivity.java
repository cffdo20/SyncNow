package br.edu.ifam.syncnow.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import br.edu.ifam.syncnow.R;
import br.edu.ifam.syncnow.entity.MaterialEstudo;
import br.edu.ifam.syncnow.repository.MaterialDAO;

public class DetalhesMaterialActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    MaterialDAO materialDAO;
    MaterialEstudo material;
    long id = 0;
    String arquivoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhe_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        materialDAO = new MaterialDAO(this);
        id = getIntent().getLongExtra("id", id);
        material = materialDAO.getMaterial(id);

        String titulo = material.getTitulo();
        String descricao = material.getDescricao();
        String disciplina = material.getDisciplina();
        String tag = material.getTag();
        arquivoUri = material.getArquivoUri();

        // Referenciando os TextViews
        TextView tituloTextView = findViewById(R.id.tituloTextView);
        TextView descricaoTextView = findViewById(R.id.descricaoTextView);
        TextView disciplinaTextView = findViewById(R.id.disciplinaTextView);
        TextView tagTextView = findViewById(R.id.tagTextView);
        ImageView imageView = findViewById(R.id.imageView);

        // Definindo os valores nos TextViews
        if (titulo != null) {
            tituloTextView.setText("Titulo: " + titulo);
            descricaoTextView.setText("Descricao: " + descricao);
            disciplinaTextView.setText("Disciplina: " + disciplina);
            tagTextView.setText("Tag: " + tag);
            int[] dimensoes = obterDimensoesImagem(arquivoUri);
            carregarImagem(arquivoUri, imageView, 256, 256);
        }
    }

    private int[] obterDimensoesImagem(String filePath) {
        // Criar um objeto BitmapFactory.Options
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Definir inJustDecodeBounds como true para obter apenas as dimensões sem carregar a imagem inteira
        options.inJustDecodeBounds = true;

        // Decodificar o arquivo para obter as dimensões
        BitmapFactory.decodeFile(filePath, options);

        // Obter as dimensões da imagem
        int largura = options.outWidth;
        int altura = options.outHeight;

        // Retornar as dimensões como um array de inteiros [largura, altura]
        return new int[]{largura, altura};
    }
    private void carregarImagem(String filePath, ImageView imageView, int largura, int altura) {
        File file = new File(filePath);  // Criando o objeto File a partir do caminho

        if (file.exists()) {
            try {
                // Abrindo o arquivo diretamente com FileInputStream
                FileInputStream fileInputStream = new FileInputStream(file);

                // Decodificando o InputStream para Bitmap
                Bitmap bitmapOriginal = BitmapFactory.decodeStream(fileInputStream);

                // Verificando se o Bitmap foi carregado corretamente
                if (bitmapOriginal != null) {
                    // Redimensionando o Bitmap
                    Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, largura, altura, true);

                    // Exibindo a imagem no ImageView
                    imageView.setImageBitmap(bitmapRedimensionado);

                    // Fechando o FileInputStream
                    fileInputStream.close();
                } else {
                    Log.e("DetalhesMaterialActivity", "Erro ao decodificar a imagem");
                    Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                Log.e("DetalhesMaterialActivity", "Erro ao carregar a imagem", e);
                Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("DetalhesMaterialActivity", "Arquivo não encontrado: " + filePath);
            Toast.makeText(this, "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
        }
    }



    public void voltarOnClick(View view) {
        // Chama finish() para voltar à Activity anterior
        finish();
    }
}