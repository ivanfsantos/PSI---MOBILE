package pt.ipleiria.estg.dei.boleias.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.R;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;


public class ListaViaturasAdaptador extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Viatura> viaturas;

    public ListaViaturasAdaptador(Context context, ArrayList<Viatura> viaturas){
        this.context = context;
        this.viaturas = viaturas;
    }

    @Override
    public int getCount()
    {
        return viaturas.size();
    }

    @Override
    public Object getItem(int i)
    {
        return viaturas.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return viaturas.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Viatura viatura = viaturas.get(i);
        if (inflater == null){
            inflater = LayoutInflater.from(context);

        }

        if (view == null){
            view = inflater.inflate(R.layout.item_lista_viatura, null);
        }

        ViewHolderLista viewHolder = (ViewHolderLista) view.getTag();
        if (viewHolder == null){
            viewHolder = new ViewHolderLista(view);
            view.setTag(viewHolder);
        }

        viewHolder.update(viatura);

        return view;
    }

    private class ViewHolderLista{


        private TextView tvMarca, tvModelo, tvMatricula, tvCor, tvPerfil_id;

        public ViewHolderLista(View view) {
            tvMarca = view.findViewById(R.id.etMarca);
            tvModelo = view.findViewById(R.id.etModelo);
            tvMatricula = view.findViewById(R.id.etMatricula);
            tvCor = view.findViewById(R.id.etCor);
            //tvPerfil_id = view.findViewById(R.id.etPerfil_id);
        }

        public void update(Viatura viatura){
            tvMarca.setText(viatura.getMarca());
            tvModelo.setText(viatura.getModelo());
            tvMatricula.setText(viatura.getMatricula());
            tvCor.setText(viatura.getCor());
            //tvPerfil_id.setText(viatura.getPerfil_id()+"");

        }
    }
}
