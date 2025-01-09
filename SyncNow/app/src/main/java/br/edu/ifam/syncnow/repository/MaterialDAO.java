package br.edu.ifam.syncnow.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifam.syncnow.entity.MaterialEstudo;

public class MaterialDAO {
    private SQLiteDatabase sqLiteDatabase;

    private MaterialDAO() {}

    public MaterialDAO(Context context){
        BDMateriais bdMateriais = new
                BDMateriais(context, "materiais", null, 1);

        sqLiteDatabase = bdMateriais.getWritableDatabase();
    }

    @SuppressLint("Range")
    public List<MaterialEstudo> getMaterial() {
        List<MaterialEstudo> materiais = new ArrayList<>();
        String sql = "SELECT * FROM materiais";
        Cursor cMateriais = sqLiteDatabase.rawQuery(sql, null);
        if (cMateriais.moveToFirst()) {
            do {
                MaterialEstudo material = new MaterialEstudo();
                material.setId(cMateriais.getLong(cMateriais.getColumnIndex("id")));
                material.setTitulo(cMateriais.getString(cMateriais.getColumnIndex("titulo")));
                material.setDescricao(cMateriais.getString(cMateriais.getColumnIndex("descricao")));
                material.setDisciplina(cMateriais.getString(cMateriais.getColumnIndex("disciplina")));
                material.setTag(cMateriais.getString(cMateriais.getColumnIndex("tag")));

                // Lê o campo arquivoUri (URI)
                String arquivoUriString = cMateriais.getString(cMateriais.getColumnIndex("arquivo"));
                material.setArquivoUri(arquivoUriString); // Supondo que você tenha um setter para o URI

                materiais.add(material);
            } while (cMateriais.moveToNext());
        }
        return materiais;
    }
    @SuppressLint("Range")
    public MaterialEstudo getMaterial(long id) {
        String sql = "SELECT * FROM materiais WHERE id = ?";
        String[] selectionArgs = {Long.toString(id)};
        Cursor cMaterial = sqLiteDatabase.rawQuery(sql, selectionArgs);
        MaterialEstudo material = new MaterialEstudo();
        if (cMaterial.moveToFirst()) {
            material.setId(cMaterial.getLong(cMaterial.getColumnIndex("id")));
            material.setTitulo(cMaterial.getString(cMaterial.getColumnIndex("titulo")));
            material.setDescricao(cMaterial.getString(cMaterial.getColumnIndex("descricao")));
            material.setDisciplina(cMaterial.getString(cMaterial.getColumnIndex("disciplina")));
            material.setTag(cMaterial.getString(cMaterial.getColumnIndex("tag")));

            // Lê o campo arquivoUri (URI)
            String arquivoUriString = cMaterial.getString(cMaterial.getColumnIndex("arquivo"));
            material.setArquivoUri(arquivoUriString); // Supondo que você tenha um setter para o URI
        }
        return material;
    }
    public void insert(MaterialEstudo material){
        ContentValues cv = new ContentValues();
        cv.put("titulo", material.getTitulo());
        cv.put("descricao", material.getDescricao());
        cv.put("disciplina", material.getDisciplina());
        cv.put("tag", material.getTag());
        Log.d("ArquivoURI", "URI do arquivo: " + material.getArquivoUri().toString());
        // Salvar a URI do arquivo, se disponível
        if (material.getArquivoUri() != null) {
            cv.put("arquivo", material.getArquivoUri().toString());
        }

        sqLiteDatabase.insert("materiais", null, cv);
    }
    public void update(long id, MaterialEstudo material) {
        ContentValues cv = new ContentValues();
        cv.put("titulo", material.getTitulo());
        cv.put("descricao", material.getDescricao());
        cv.put("disciplina", material.getDisciplina());
        cv.put("tag", material.getTag());

        // Atualiza o URI do arquivo, se presente
        if (material.getArquivoUri() != null) {
            cv.put("arquivo", material.getArquivoUri());  // Armazena o URI como string
        }

        String whereClause = "id = ?";
        String[] whereArgs = {Long.toString(id)};

        sqLiteDatabase.update("materiais", cv, whereClause, whereArgs);
    }
    public void delete(long id){
        String whereClause = "id = ?";
        String[] whereArgs = {Long.toString(id)};
        sqLiteDatabase.delete("materiais", whereClause, whereArgs);
    }
}
