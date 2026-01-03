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


public class ListaBoleiasAdaptador extends BaseAdapter {



    Context context;
    LayoutInflater inflater;
    ArrayList<Boleia> boleias;

    public ListaBoleiasAdaptador(Context context, ArrayList<Boleia> boleias){
        this.context = context;
        this.boleias = boleias;
    }


    @Override
    public int getCount()
    {
        return boleias.size();
    }

    @Override
    public Object getItem(int i)
    {
        return boleias.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return boleias.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Boleia boleia = boleias.get(i);
        if (inflater == null){
            inflater = LayoutInflater.from(context);

        }

        if (view == null){
            view = inflater.inflate(R.layout.item_lista_boleia, null);
        }

        ListaBoleiasAdaptador.ViewHolderLista viewHolder = (ListaBoleiasAdaptador.ViewHolderLista) view.getTag();
        if (viewHolder == null){
            viewHolder = new ListaBoleiasAdaptador.ViewHolderLista(view);
            view.setTag(viewHolder);
        }

        viewHolder.update(boleia);

        return view;
    }

    private class ViewHolderLista{


        private TextView tvOrigem, tvDestino, tvData_hora, tvLugares_disponiveis, tvPreco, tvViatura_id;

        public ViewHolderLista(View view) {
            tvOrigem = view.findViewById(R.id.etOrigem);
            tvDestino = view.findViewById(R.id.etDestino);
            tvData_hora = view.findViewById(R.id.etData_hora);
            tvLugares_disponiveis = view.findViewById(R.id.etLugares_disponiveis);
            tvPreco = view.findViewById(R.id.etPreco);
            tvViatura_id = view.findViewById(R.id.etViatura_id);
        }

        public void update(Boleia boleia){

            tvOrigem.setText(boleia.getOrigem());
            tvDestino.setText(boleia.getDestino());
            tvData_hora.setText(boleia.getData_hora());
            tvLugares_disponiveis.setText(boleia.getLugares_disponiveis() + "");
            tvPreco.setText(boleia.getPreco() + "€");
            tvViatura_id.setText(boleia.getViatura_id() + "");
        }
    }
}
