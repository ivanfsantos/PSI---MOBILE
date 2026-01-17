package pt.ipleiria.estg.dei.boleias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.adaptadores.ListaVerReservasAdaptador;
import pt.ipleiria.estg.dei.boleias.listeners.ReservasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;


public class ListaVerReservasFragment extends Fragment implements ReservasListener {

    private int boleia_id;
    private ListView lvReservasCondutor;
    private FloatingActionButton fabValidar;
    private String token;
    private ListaVerReservasAdaptador adaptador;




    public ListaVerReservasFragment()
    {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_ver_reservas, container, false);

        if (getArguments() != null) {
            boleia_id = getArguments().getInt("BOLEIA_ID", -1);
        }


        lvReservasCondutor = view.findViewById(R.id.lvReservasCondutor);
        fabValidar = view.findViewById(R.id.fabValidar);


        setHasOptionsMenu(true);
        getInfo();

        Singleton.getInstance(getContext()).setReservasListener(this);
        Singleton.getInstance(getContext()).getAllReservasCondutorAPI(getContext(), token, String.valueOf(boleia_id));

        fabValidar.setImageResource(R.drawable.ic_action_validar);
        fabValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton.getInstance(getContext()).validarReservasAPI(getContext(), token, String.valueOf(boleia_id));

            }
        });


        return view;
    }

    private void getInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
    }



    @Override
    public void onRefreshListaReservas(ArrayList<Reserva> listaReservas) {
        if (listaReservas != null) {
            adaptador = new ListaVerReservasAdaptador(getContext(), listaReservas);
            lvReservasCondutor.setAdapter(adaptador);
        }
        if (!listaReservas.isEmpty() && "Pago".equals(listaReservas.get(0).getEstado())) {

            if (getActivity() != null) {
                getActivity().setResult(android.app.Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }
}