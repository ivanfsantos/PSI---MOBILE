package pt.ipleiria.estg.dei.boleias;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ipleiria.estg.dei.boleias.listeners.ViaturaListener;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;

public class DetalhesViaturaActivity extends AppCompatActivity implements ViaturaListener {

    public static final String VIATURA_ID = "viatura_id";

    private Viatura viatura;
    Integer idViatura = -1;

    EditText etMarca, etModelo, etMatricula, etCor, etPerfil_id;

    private FloatingActionButton fabGuardar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String token;
    private String perfil_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_viatura);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getToken();

        viatura = Singleton.getInstance(getApplicationContext()).getViatura(idViatura);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        etMatricula = findViewById(R.id.etMatricula);
        etCor = findViewById(R.id.etCor);
        fabGuardar = findViewById(R.id.fabGuardar);
        etPerfil_id = findViewById(R.id.etPerfil_id);
        etPerfil_id.setVisibility(View.GONE);
        etPerfil_id.setEnabled(false);

        Singleton.getInstance(getApplicationContext()).setViaturaListener(this);


        idViatura = getIntent().getIntExtra("viatura_id", -1);

        if (idViatura!=-1){
            viatura = Singleton.getInstance(getApplicationContext()).getViatura(idViatura);
            setTitle(getString(R.string.txt_detalhes) + " " + viatura.getMatricula());
            etMarca.setText(viatura.getMarca());
            etModelo.setText(viatura.getModelo());
            etMatricula.setText(viatura.getMatricula());
            etCor.setText(viatura.getCor());
            etPerfil_id.setText(viatura.getPerfil_id() + "");

            fabGuardar.setImageResource(R.drawable.ic_action_guardar);
        }
        else {
            setTitle(getString(R.string.txt_adicionar_viatura));
            fabGuardar.setImageResource(R.drawable.ic_action_add);
        }

        fabGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(idViatura!=-1){
                    if(isViaturaValido()){
                        viatura.setMarca(etMarca.getText().toString());
                        viatura.setModelo(etModelo.getText().toString());
                        viatura.setMatricula(etMatricula.getText().toString());
                        viatura.setCor(etCor.getText().toString());

                        Singleton.getInstance(getApplicationContext()).editarViaturaAPI(token, viatura, getApplicationContext());

                    }
                }else if(isViaturaValido()){

                    viatura = new Viatura(
                            0,
                            etMarca.getText().toString(),
                            etModelo.getText().toString(),
                            etMatricula.getText().toString(),
                            etCor.getText().toString(),
                            Integer.parseInt(perfil_id)
                    );
                    Singleton.getInstance(getApplicationContext()).adicionarViaturaAPI(token, viatura, getApplicationContext());

                }

            }
        });

    }

    private void getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);
    }



    private boolean isViaturaValido() {

        String marca = etMarca.getText().toString();
        String modelo = etModelo.getText().toString();
        String matricula = etMatricula.getText().toString();
        String cor = etCor.getText().toString();

        if (marca.isEmpty()) {
            etMarca.setError("Marca obrigatória");
            return false;
        }
        if (modelo.isEmpty()) {
            etModelo.setError("Modelo obrigatório");
            return false;
        }

        if (cor.isEmpty()) {
            etCor.setError("Cor obrigatória");
            return false;
        }

        if (Singleton.getInstance(getApplicationContext()).matriculaExiste(matricula, idViatura)) {
            etMatricula.setError("Esta matrícula já está registada!");
            return false;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (idViatura!=-1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_remover, menu);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemRemover){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.txt_remover_viatura)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage(R.string.txt_tem_a_certeza_que_pretende_remover_a_viatura)
                    .setPositiveButton(getString(R.string.txt_sim), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Singleton.getInstance(getApplicationContext()).removerViaturaAPI(token, viatura, getApplicationContext());

                        }
                    })
                    .setNegativeButton(getString(R.string.txt_cancelar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefreshDetalhes(int op) {
        Intent intent = new Intent();
        intent.putExtra("OPERACAO", op); // Passa MenuMainActivity.EDIT ou MenuMainActivity.DEL
        setResult(RESULT_OK, intent);
        finish();
    }
}