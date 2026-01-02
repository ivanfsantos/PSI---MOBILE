package pt.ipleiria.estg.dei.boleias;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

import pt.ipleiria.estg.dei.boleias.adaptadores.ListaViaturasAdaptador;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;


public class ListaViaturasFragment extends Fragment implements ViaturasListener {

    private ListView lvViaturas;
    private String token;
    private String perfil_id;
    private FloatingActionButton fabLista;
    private ListaViaturasAdaptador adaptador;
    private SearchView searchView;



    public ListaViaturasFragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_viaturas, container, false);

        lvViaturas = view.findViewById(R.id.lvViaturas);
        fabLista = view.findViewById(R.id.fabLista);

        setHasOptionsMenu(true);

        Singleton.getInstance(getContext()).setViaturasListener(this);

        SharedPreferences sharedPref = getContext().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", null);
        perfil_id = sharedPref.getString("perfil_id", null);

        if (token != null && perfil_id != null) {
            Singleton.getInstance(getContext()).getAllViaturasAPI(getContext(), token, perfil_id);
        }

        fabLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), DetalhesViaturaActivity.class);
                startActivityForResult(intent, MenuMainActivity.ADD);
            }
        });

        lvViaturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent (getContext(), DetalhesViaturaActivity.class);
                intent.putExtra(DetalhesViaturaActivity.VIATURA_ID, (int) id);
                startActivityForResult(intent, MenuMainActivity.EDIT);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == Activity.RESULT_OK){

            Singleton.getInstance(getContext()).getAllViaturasAPI(getContext(), token, perfil_id);

            if (requestCode == MenuMainActivity.EDIT && data != null) {
                int operacaoRealizada = data.getIntExtra("OPERACAO", MenuMainActivity.EDIT);

                if (operacaoRealizada == MenuMainActivity.DEL) {
                    Toast.makeText(getContext(), R.string.viatura_removida_com_sucesso, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.viatura_modificada_com_sucesso, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == MenuMainActivity.ADD) {
                Toast.makeText(getContext(), R.string.viatura_adicionada_com_sucesso, Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_pesquisa, menu);
        MenuItem itemPesquisa = menu.findItem(R.id.itemPesquisa);
        searchView = (SearchView) itemPesquisa.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Viatura> tempViaturas = new ArrayList<>();
                for (Viatura viatura: Singleton.getInstance(getContext()).getViaturasBD()) {
                    if (viatura.getMarca().toLowerCase().contains(newText.toLowerCase())){
                        tempViaturas.add(viatura);
                    }
                }
                adaptador = new ListaViaturasAdaptador(getContext(), tempViaturas);
                lvViaturas.setAdapter(adaptador);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

    }


    @Override
    public void onRefreshListaViaturas(ArrayList<Viatura> listaViaturas) {
        lvViaturas.setAdapter(new ListaViaturasAdaptador(getContext(), listaViaturas));
    }
}