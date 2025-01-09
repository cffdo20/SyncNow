package br.edu.ifam.syncnow.entity;

import android.net.Uri;

import java.net.URI;
import java.util.List;

public class MaterialEstudo {
    private long id;
    private String titulo;
    private String disciplina;
    private String descricao;
    private String tag;
    private String arquivoUri;

    public MaterialEstudo(){}

    public MaterialEstudo(String titulo, String disciplina, String tag, String descricao) {
        this.titulo = titulo;
        this.disciplina = disciplina;
        this.tag = tag;
        this.descricao = descricao;
    }

    public MaterialEstudo(long id, String titulo, String disciplina, String tag, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.disciplina = disciplina;
        this.tag = tag;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArquivoUri() {
        return arquivoUri;
    }

    public void setArquivoUri(String arquivoUri) {
        this.arquivoUri = arquivoUri;
    }
}
