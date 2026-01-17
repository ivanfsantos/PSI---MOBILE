package pt.ipleiria.estg.dei.boleias;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ipleiria.estg.dei.boleias.listeners.BoleiaListener;
import pt.ipleiria.estg.dei.boleias.listeners.DestinoFavoritoListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturaListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.DestinoFavorito;
import pt.ipleiria.estg.dei.boleias.modelos.Reserva;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class DetalhesDestinoFavoritoActivity extends AppCompatActivity implements DestinoFavoritoListener, BoleiaListener, ViaturaListener {

    public static final String DESTINO_FAVORITO_ID = "destino_favorito_id";

    private DestinoFavorito destinoFavorito;
    private Boleia boleia;
    Integer idDestinoFavorito = -1;
    private FragmentManager fragmentManager;


    EditText etOrigem, etDestino, etData_hora;
    private FloatingActionButton fabRemover;
    private FloatingActionButton fabReservar;
    private String token;
    private String perfil_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_destino_favorito);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();

                    findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
                    findViewById(R.id.contentFragment).setVisibility(View.GONE);

                    fabReservar.show();
                    fabRemover.show();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        fragmentManager = getSupportFragmentManager();

        getInfo();

        Singleton.getInstance(this).setDestinoFavoritoListener(this);

        etOrigem = findViewById(R.id.etOrigem);
        etDestino = findViewById(R.id.etDestino);
        etData_hora = findViewById(R.id.etData_hora);

        fabRemover = findViewById(R.id.fabRemover);
        fabReservar = findViewById(R.id.fabReservar);

        fabRemover.setImageResource(R.drawable.ic_action_remover);
        fabReservar.setImageResource(R.drawable.ic_action_reservar);

        idDestinoFavorito = getIntent().getIntExtra(DESTINO_FAVORITO_ID, -1);


        if (idDestinoFavorito != -1) {
            destinoFavorito = Singleton.getInstance(getApplicationContext()).getDestinoFavorito(idDestinoFavorito);

            this.boleia = Singleton.getInstance(this).getBoleia(destinoFavorito.getBoleia_id());

            if (this.boleia != null) {
                setTitle(getString(R.string.txt_detalhes) + " " + boleia.getOrigem() + " -> " + boleia.getDestino());
                etOrigem.setText(boleia.getOrigem());
                etDestino.setText(boleia.getDestino());
                etData_hora.setText(boleia.getData_hora());
            }

            setInputsEnabled(false);
        }

        fabReservar.setOnClickListener(v -> {
            if (this.boleia != null) {
                findViewById(R.id.mainContent).setVisibility(View.GONE);
                findViewById(R.id.contentFragment).setVisibility(View.VISIBLE);
                fabReservar.hide();
                fabRemover.hide();

                ReservarFragment fragment = new ReservarFragment();
                Bundle args = new Bundle();
                args.putInt("boleia_id", boleia.getId());
                args.putInt("perfil_id", Integer.parseInt(perfil_id));
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentFragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        fabRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance(getApplicationContext()).removerDestinoFavoritoAPI(token, destinoFavorito, getApplicationContext());
            }
        });

    }




    private void getInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);
    }

    private void setInputsEnabled(boolean enabled) {
        View[] views = {etOrigem, etDestino, etData_hora};
        for (View v : views) {
            if (v != null) {
                v.setEnabled(enabled);

            }
        }
    }



    @Override
    public void onRefreshDetalhes(int op) {
        Intent intent = new Intent();
        intent.putExtra("OPERACAO", op);
        setResult(RESULT_OK, intent);
        finish();
    }
}