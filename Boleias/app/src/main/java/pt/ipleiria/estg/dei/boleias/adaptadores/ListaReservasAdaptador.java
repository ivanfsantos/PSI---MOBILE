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
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class ListaReservasAdaptador extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Reserva> reservas;

    public ListaReservasAdaptador(Context context, ArrayList<Reserva> reservas){
        this.context = context;
        this.reservas = reservas;
    }


    @Override
    public int getCount()
    {
        return reservas.size();
    }

    @Override
    public Object getItem(int i)
    {
        return reservas.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return reservas.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Reserva reserva = reservas.get(i);
        if (inflater == null){
            inflater = LayoutInflater.from(context);

        }

        if (view == null){
            view = inflater.inflate(R.layout.item_lista_reserva, null);
        }

        ListaReservasAdaptador.ViewHolderLista viewHolder = (ListaReservasAdaptador.ViewHolderLista) view.getTag();
        if (viewHolder == null){
            viewHolder = new ListaReservasAdaptador.ViewHolderLista(view);
            view.setTag(viewHolder);
        }

        viewHolder.update(reserva);

        return view;
    }

    private class ViewHolderLista{


        private TextView tvOrigem, tvDestino, tvData_hora, tvPonto_encontro, tvReembolso, TvEstado;

        public ViewHolderLista(View view) {
            tvOrigem = view.findViewById(R.id.etOrigem);
            tvDestino = view.findViewById(R.id.etDestino);
            tvData_hora = view.findViewById(R.id.etData_hora);
            tvPonto_encontro = view.findViewById(R.id.etPontoEncontro);
            tvReembolso = view.findViewById(R.id.etReembolso);
            TvEstado = view.findViewById(R.id.etEstado);
        }

       public void update(Reserva reserva){

           Boleia boleia = Singleton.getInstance(context).getBoleia(reserva.getBoleia_id());

           if(boleia != null) {
               tvOrigem.setText(boleia.getOrigem());
               tvDestino.setText(boleia.getDestino());
               tvData_hora.setText(boleia.getData_hora());
           }else {
               tvOrigem.setText("Origem...");
               tvDestino.setText("Destino...");
               tvData_hora.setText("Data...");
           }
            tvPonto_encontro.setText(reserva.getPonto_encontro() + "");
            tvReembolso.setText(reserva.getReembolso() + "€");
            TvEstado.setText(reserva.getEstado());

        }
    }
}
