package pt.ipleiria.estg.dei.boleias;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.adaptadores.ListaBoleiasAdaptador;
import pt.ipleiria.estg.dei.boleias.adaptadores.ListaReservasAdaptador;
import pt.ipleiria.estg.dei.boleias.listeners.BoleiasListener;
import pt.ipleiria.estg.dei.boleias.listeners.ReservasListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;


public class ListaReservasFragment extends Fragment implements ReservasListener, BoleiasListener, ViaturasListener {

    private ListView lvReservas;
    private ListaReservasAdaptador adaptador;
    private String token;
    private String perfil_id;
    private String condutor;


    public ListaReservasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_reservas, container, false);

        lvReservas = view.findViewById(R.id.lvReservas);

        setHasOptionsMenu(true);
        getInfo();

        Singleton.getInstance(getContext()).setBoleiasListener(this);
        Singleton.getInstance(getContext()).setViaturasListener(this);
        Singleton.getInstance(getContext()).setReservasListener(this);
        Singleton.getInstance(getContext()).getAllBoleiasAPI(getContext(), token);
        Singleton.getInstance(getContext()).getAllViaturasAPI(getContext(), token);
        Singleton.getInstance(getContext()).getAllReservasAPI(getContext(), token, perfil_id);


        lvReservas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent (getContext(), DetalhesReservaActivity.class);
                intent.putExtra(DetalhesReservaActivity.RESERVA_ID, (int) id);
                startActivityForResult(intent, MenuMainActivity.DEL);
            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK){

            Singleton.getInstance(getContext()).getAllReservasAPI(getContext(), token, perfil_id);

            if (requestCode == MenuMainActivity.EDIT && data != null) {
                int operacaoRealizada = data.getIntExtra("OPERACAO", MenuMainActivity.EDIT);

                if (operacaoRealizada == MenuMainActivity.DEL) {
                    Toast.makeText(getContext(), R.string.reserva_removida_com_sucesso, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


    private void getInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);
        condutor = sharedPreferences.getString("condutor",null);
    }

    @Override
    public void onRefreshListaReservas(ArrayList<Reserva> listaReservas) {
        if (listaReservas != null) {
            adaptador = new ListaReservasAdaptador(getContext(), listaReservas);
            lvReservas.setAdapter(adaptador);
        }
    }

    @Override
    public void onRefreshListaBoleias(ArrayList<Boleia> listaBoleias) {
        if (lvReservas.getAdapter() != null) {
            ((ListaReservasAdaptador) lvReservas.getAdapter()).notifyDataSetChanged();
        }
    }


    @Override
    public void onRefreshListaViaturas(ArrayList<Viatura> listaViaturas) {
        if (lvReservas.getAdapter() != null) {
            ((ListaReservasAdaptador) lvReservas.getAdapter()).notifyDataSetChanged();
        }

    }
}