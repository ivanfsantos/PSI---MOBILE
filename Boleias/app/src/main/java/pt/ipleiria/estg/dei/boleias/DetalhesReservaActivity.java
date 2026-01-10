package pt.ipleiria.estg.dei.boleias;

import static android.view.View.GONE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;

import pt.ipleiria.estg.dei.boleias.listeners.BoleiasListener;
import pt.ipleiria.estg.dei.boleias.listeners.ReservaListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;


public class DetalhesReservaActivity extends AppCompatActivity implements ReservaListener, BoleiasListener, ViaturasListener {

    public static final String RESERVA_ID = "reserva_id";
    private Reserva reserva;
    private Boleia boleia;
    Integer idReserva = -1;
    private FragmentManager fragmentManager;


    EditText etOrigem, etDestino, etData_hora, etPontoEncontro, etReembolso, etEstado;
    private FloatingActionButton fabRemover;
    private FloatingActionButton fabAvaliarCondutor;
    private String token;
    private String perfil_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_reserva);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();

                    findViewById(R.id.mainContent).setVisibility(View.VISIBLE);

                    fabAvaliarCondutor.show();
                    fabRemover.show();

                    findViewById(R.id.contentFragment).setVisibility(View.GONE);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        fragmentManager = getSupportFragmentManager();

        getInfo();

        Singleton.getInstance(this).getAllViaturasAPI(this, token);
        Singleton.getInstance(this).getAllBoleiasAPI(this, token);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etOrigem = findViewById(R.id.etOrigem);
        etDestino = findViewById(R.id.etDestino);
        etData_hora = findViewById(R.id.etData_hora);
        etPontoEncontro = findViewById(R.id.etDescricao);
        etReembolso = findViewById(R.id.etReembolso);
        etEstado = findViewById(R.id.etEstado);
        fabRemover = findViewById(R.id.fabRemover);
        fabAvaliarCondutor = findViewById(R.id.fabAvaliarCondutor);

        Singleton.getInstance(this).setReservaListener(this);
        Singleton.getInstance(this).setBoleiasListener(this);
        Singleton.getInstance(this).setViaturasListener(this);

        fabRemover.setImageResource(R.drawable.ic_action_remover);
        fabAvaliarCondutor.setImageResource(R.drawable.ic_action_avaliar_condutor);

        idReserva = getIntent().getIntExtra(RESERVA_ID, -1);

        if (idReserva != -1) {
            reserva = Singleton.getInstance(this).getReserva(idReserva);
            boleia = Singleton.getInstance(this).getBoleia(reserva.getBoleia_id());

            setTitle(getString(R.string.txt_detalhes) + " da " + "Reserva");

            setInputsEnabled(false);

            etOrigem.setText(boleia.getOrigem());
            etDestino.setText(boleia.getDestino());
            etData_hora.setText(boleia.getData_hora());
            etPontoEncontro.setText(reserva.getPonto_encontro());
            etReembolso.setText(String.valueOf(reserva.getReembolso()));
            etEstado.setText(reserva.getEstado());
        }

        fabRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance(getApplicationContext()).removerReservaAPI(token, reserva, getApplicationContext());
            }
        });

        fabAvaliarCondutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    {
                        if (reserva != null) {
                            findViewById(R.id.mainContent).setVisibility(View.GONE);
                            fabAvaliarCondutor.hide();
                            fabRemover.hide();

                            View fragmentContainer = findViewById(R.id.contentFragment);
                            fragmentContainer.setVisibility(View.VISIBLE);

                            AvaliarFragment fragment = new AvaliarFragment();
                            Bundle args = new Bundle();
                            args.putInt("boleia_id", boleia.getId());
                            fragment.setArguments(args);

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.contentFragment, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            }
        });


    }



    private void getInfo() {

        SharedPreferences sharedPreferences = getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);

    }

    private void setInputsEnabled(boolean enabled) {
        View[] views = {etOrigem, etDestino, etData_hora, etPontoEncontro, etReembolso, etEstado};
        for (View v : views) {
            if (v != null) {
                v.setEnabled(enabled);

            }
        }
    }

    @Override
    public void onRefreshDetalhes(int op) {
        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void onRefreshListaBoleias(ArrayList<Boleia> listaBoleias) {

    }




    @Override
    public void onRefreshListaViaturas(ArrayList<Viatura> listaViaturas) {

    }
}