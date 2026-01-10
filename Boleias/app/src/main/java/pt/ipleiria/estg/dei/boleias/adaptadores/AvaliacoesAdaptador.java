package pt.ipleiria.estg.dei.boleias.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.modelos.Avaliacao;

public class AvaliacoesAdaptador extends RecyclerView.Adapter<AvaliacoesAdaptador.ViewHolder> {
    private Context context;
    private ArrayList<Avaliacao> avaliacoes;

    public AvaliacoesAdaptador(Context context, ArrayList<Avaliacao> avaliacoes) {
        this.context = context;
        this.avaliacoes = avaliacoes;
    }

    public void updateAvaliacoes(ArrayList<Avaliacao> novasAvaliacoes) {
        this.avaliacoes = novasAvaliacoes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Avaliacao a = avaliacoes.get(position);

        if (a != null) {
            holder.tvComentario.setText(a.getDescricao());

        }
    }

    @Override
    public int getItemCount() {
        return avaliacoes != null ? avaliacoes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComentario, tvNota;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // No layout simple_list_item_2, o text1 é o título e o text2 é o subtítulo
            tvComentario = itemView.findViewById(android.R.id.text1);
            tvNota = itemView.findViewById(android.R.id.text2);
        }
    }
}
