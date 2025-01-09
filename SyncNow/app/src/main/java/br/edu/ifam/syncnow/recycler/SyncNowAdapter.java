package br.edu.ifam.syncnow.recycler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.List;

import br.edu.ifam.syncnow.R;
import br.edu.ifam.syncnow.activity.DetalhesMaterialActivity;
import br.edu.ifam.syncnow.entity.MaterialEstudo;

public class SyncNowAdapter extends RecyclerView.Adapter<SyncNowAdapter.ViewHolder>{
    private List<MaterialEstudo> materiais;
    private Context context;

    public SyncNowAdapter(List<MaterialEstudo> materiais, Context context) {
        this.materiais = materiais;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MaterialEstudo material = materiais.get(position);
        if (material == null || material.getTitulo() == null || material.getDisciplina() == null) {
            Log.e("SyncNowAdapter", "Dados inválidos na posição: " + position);
            return;
        }
        if (holder.tituloTextView != null && holder.disciplinaTextView != null) {
            holder.tituloTextView.setText(material.getTitulo());
            holder.disciplinaTextView.setText(String.valueOf(material.getDisciplina()));
        } else {
            Log.e("SyncNowAdapter", "TextViews são nulos na posição: " + position);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesMaterialActivity.class);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("id", material.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return materiais.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView disciplinaTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.item_titulo);
            disciplinaTextView = itemView.findViewById(R.id.item_disciplina);
        }
    }

    public void updateData(List<MaterialEstudo> novosMateriais) {
        this.materiais.clear();
        this.materiais.addAll(novosMateriais);
        notifyDataSetChanged();
    }

}
