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

import pt.ipleiria.estg.dei.boleias.adaptadores.ListaBoleiasAdaptador;
import pt.ipleiria.estg.dei.boleias.listeners.BoleiasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class ListaBoleiasFragment extends Fragment implements BoleiasListener {

    private ListView lvBoleias;
    private FloatingActionButton fabLista;
    private ListaBoleiasAdaptador adaptador;
    private SearchView searchView;
    private String token;
    private String condutor;








    public ListaBoleiasFragment()
    {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_boleias_condutor, container, false);

        lvBoleias = view.findViewById(R.id.lvBoleias);

        fabLista = view.findViewById(R.id.fabLista);

        setHasOptionsMenu(true);
        getInfo();
        Singleton.getInstance(getContext()).setBoleiasListener(this);
        Singleton.getInstance(getContext()).getAllBoleiasAPI(getContext(), token);


        if("1".equals(condutor)) {
            setFabVisibility(true);
            fabLista.setImageResource(R.drawable.ic_action_add);
            fabLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DetalhesBoleiaActivity.class);
                    startActivityForResult(intent, MenuMainActivity.ADD);
                }
            });
        } else {
            setFabVisibility(false);
        }

        lvBoleias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent (getContext(), DetalhesBoleiaActivity.class);
                intent.putExtra(DetalhesBoleiaActivity.BOLEIA_ID, (int) id);
                startActivityForResult(intent, MenuMainActivity.EDIT);
            }
        });

        return view;
    }

    private void getInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        condutor = sharedPreferences.getString("condutor", null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK){

            Singleton.getInstance(getContext()).getAllBoleiasAPI(getContext(), token);

            if (requestCode == MenuMainActivity.EDIT && data != null) {
                int operacaoRealizada = data.getIntExtra("OPERACAO", MenuMainActivity.EDIT);

                if (operacaoRealizada == MenuMainActivity.DEL) {
                    Toast.makeText(getContext(), R.string.boleia_removida_com_sucesso, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.boleia_modificada_com_sucesso, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == MenuMainActivity.ADD) {
                Toast.makeText(getContext(), R.string.boleia_adicionada_com_sucesso, Toast.LENGTH_SHORT).show();
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
                ArrayList<Boleia> tempBoleias = new ArrayList<>();
                for (Boleia boleia: Singleton.getInstance(getContext()).getBoleiasBD()) {
                    if (boleia.getDestino().toLowerCase().contains(newText.toLowerCase()) ||
                            boleia.getOrigem().toLowerCase().contains(newText.toLowerCase())){
                        tempBoleias.add(boleia);
                    }
                }
                adaptador = new ListaBoleiasAdaptador(getContext(), tempBoleias);
                lvBoleias.setAdapter(adaptador);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

    }

    @Override
    public void onRefreshListaBoleias(ArrayList<Boleia> listaBoleias) {
        lvBoleias.setAdapter(new ListaBoleiasAdaptador(getContext(), listaBoleias));

    }

    private void setFabVisibility(boolean isCondutor) {
        if (fabLista != null) {
            if (isCondutor) {
                fabLista.setVisibility(View.VISIBLE);
            } else {
                fabLista.setVisibility(View.GONE);
            }
        }
    }

}