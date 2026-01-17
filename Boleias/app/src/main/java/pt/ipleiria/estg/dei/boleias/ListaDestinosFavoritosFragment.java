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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.adaptadores.ListaDestinosFavoritosAdaptador;
import pt.ipleiria.estg.dei.boleias.adaptadores.ListaReservasAdaptador;
import pt.ipleiria.estg.dei.boleias.listeners.BoleiasListener;
import pt.ipleiria.estg.dei.boleias.listeners.DestinosFavoritosListener;
import pt.ipleiria.estg.dei.boleias.listeners.ReservasListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.DestinoFavorito;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;

public class ListaDestinosFavoritosFragment extends Fragment implements DestinosFavoritosListener {

    private ListView lvDestinosFavoritos;
    private ListaDestinosFavoritosAdaptador adaptador;
    private String token;
    private String perfil_id;


    public ListaDestinosFavoritosFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_destinos_favoritos, container, false);

        lvDestinosFavoritos = view.findViewById(R.id.lvDestinosFavoritos);

        setHasOptionsMenu(true);

        getInfo();

        Singleton.getInstance(getContext()).setDestinosFavoritosListener(this);
        Singleton.getInstance(getContext()).getAllDestinosFavoritosAPI(getContext(), token, perfil_id);
        Singleton.getInstance(getContext()).getAllBoleiasAPI(getContext(), token);


        lvDestinosFavoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent (getContext(), DetalhesDestinoFavoritoActivity.class);
                intent.putExtra(DetalhesDestinoFavoritoActivity.DESTINO_FAVORITO_ID, (int) id);
                startActivityForResult(intent, MenuMainActivity.DEL);
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Singleton.getInstance(getContext()).getAllDestinosFavoritosAPI(getContext(), token, perfil_id);

            if (data != null) {
                int operacaoRealizada = data.getIntExtra("OPERACAO", -1);
                if (operacaoRealizada == MenuMainActivity.DEL) {
                    Toast.makeText(getContext(), R.string.boleia_removida_da_wishlist, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }



    private void getInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);
    }


    @Override
    public void onRefreshListaDestinosFavoritos(ArrayList<DestinoFavorito> listaDestinosFavoritos) {
        if (listaDestinosFavoritos != null) {
            adaptador = new ListaDestinosFavoritosAdaptador(getContext(), listaDestinosFavoritos);
            lvDestinosFavoritos.setAdapter(adaptador);
        }
    }

}