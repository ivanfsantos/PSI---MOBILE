package pt.ipleiria.estg.dei.boleias.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.R;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.DestinoFavorito;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class ListaDestinosFavoritosAdaptador extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<DestinoFavorito> destinosFavoritos;

    public ListaDestinosFavoritosAdaptador(Context context, ArrayList<DestinoFavorito> destinosFavoritos){
        this.context = context;
        this.destinosFavoritos = destinosFavoritos;
    }


    @Override
    public int getCount()
    {
        return destinosFavoritos.size();
    }

    @Override
    public Object getItem(int i)
    {
        return destinosFavoritos.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return destinosFavoritos.get(i).getId();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        DestinoFavorito destinoFavorito = destinosFavoritos.get(i);
        if (inflater == null){
            inflater = LayoutInflater.from(context);

        }

        if (view == null){
            view = inflater.inflate(R.layout.item_lista_destino_favorito, null);
        }


        ListaDestinosFavoritosAdaptador.ViewHolderLista viewHolder = (ListaDestinosFavoritosAdaptador.ViewHolderLista) view.getTag();


        if (viewHolder == null){
            viewHolder = new ListaDestinosFavoritosAdaptador.ViewHolderLista(view);
            view.setTag(viewHolder);
        }

        viewHolder.update(destinoFavorito);

        return view;
    }




    private class ViewHolderLista{


        private TextView tvOrigem, tvDestino, tvData_hora;

        public ViewHolderLista(View view) {
            tvOrigem = view.findViewById(R.id.etOrigem);
            tvDestino = view.findViewById(R.id.etDestino);
            tvData_hora = view.findViewById(R.id.etData_hora);
        }

        public void update(DestinoFavorito destinoFavorito){

            Boleia boleia = Singleton.getInstance(context).getBoleia(destinoFavorito.getBoleia_id());

            if(boleia != null) {
                tvOrigem.setText(boleia.getOrigem());
                tvDestino.setText(boleia.getDestino());
                tvData_hora.setText(boleia.getData_hora());
            }else {
                tvOrigem.setText("Origem...");
                tvDestino.setText("Destino...");
                tvData_hora.setText("Data...");
            }

        }
    }
}
