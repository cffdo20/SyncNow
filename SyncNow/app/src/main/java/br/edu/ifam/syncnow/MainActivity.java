package br.edu.ifam.syncnow;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifam.syncnow.activity.CadastroMaterialActivity;
import br.edu.ifam.syncnow.entity.MaterialEstudo;
import br.edu.ifam.syncnow.recycler.SyncNowAdapter;
import br.edu.ifam.syncnow.repository.MaterialDAO;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SyncNowAdapter syncnowAdapter;
    MaterialDAO materialDAO;
    private static final int CADASTRO_MATERIAL_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        materialDAO = new MaterialDAO(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        syncnowAdapter = new SyncNowAdapter(materialDAO.getMaterial(),this);
        recyclerView.setAdapter(syncnowAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Atualiza os dados no adaptador sempre que a atividade for visível
        List<MaterialEstudo> materiais = materialDAO.getMaterial();
        if (syncnowAdapter != null) {
            syncnowAdapter.updateData(materiais); // Método para atualizar os dados no adaptador
        }
    }
    public void addMaterialOnClick(View view){
        Intent intent = new Intent(this, CadastroMaterialActivity.class);
        startActivityForResult(intent, CADASTRO_MATERIAL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o resultado é do tipo que esperamos (CadastroMaterialActivity)
        if (requestCode == CADASTRO_MATERIAL_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Consulta o banco de dados novamente para obter os dados mais recentes
                List<MaterialEstudo> materiais = materialDAO.getMaterial();

                // Atualiza o adaptador com os novos dados
                syncnowAdapter.updateData(materiais);
            }
        }
    }

}