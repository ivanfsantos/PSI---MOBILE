package pt.ipleiria.estg.dei.boleias;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import pt.ipleiria.estg.dei.boleias.listeners.BoleiaListener;
import pt.ipleiria.estg.dei.boleias.listeners.ViaturasListener;
import pt.ipleiria.estg.dei.boleias.modelos.Boleia;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;
import pt.ipleiria.estg.dei.boleias.modelos.Viatura;

public class DetalhesBoleiaActivity extends AppCompatActivity implements BoleiaListener, ViaturasListener {

    public static final String BOLEIA_ID = "boleia_id";
    private Boleia boleia;
    private Spinner spViaturas;
    private ArrayList<Viatura> viaturasDisp;
    Integer idBoleia = -1;
    private FragmentManager fragmentManager;


    EditText etOrigem, etDestino, etData_hora, etLugares_disponiveis, etPreco;
    private int idViaturaSelecionada = -1;
    private FloatingActionButton fabGuardar;
    private FloatingActionButton fabReservar;
    private String token;
    private String perfil_id;
    private String condutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_boleia);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack(); // Remove o fragmento
                    findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
                    fabReservar.setVisibility(View.VISIBLE);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        fragmentManager = getSupportFragmentManager();

        getInfo();
        Singleton.getInstance(this).getAllViaturasAPI(this, token);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etOrigem = findViewById(R.id.etOrigem);
        etDestino = findViewById(R.id.etDestino);
        etData_hora = findViewById(R.id.etData_hora);
        etLugares_disponiveis = findViewById(R.id.etLugares_disponiveis);
        etPreco = findViewById(R.id.etPreco);
        fabGuardar = findViewById(R.id.fabGuardar);
        spViaturas = findViewById(R.id.spViaturas);
        fabReservar = findViewById(R.id.fabReservar);

        Singleton.getInstance(this).setBoleiaListener(this);
        Singleton.getInstance(this).setViaturasListener(this);

        etData_hora.setOnClickListener(v -> showMaterialDateTimePicker());
        viaturasDisp = Singleton.getInstance(this).getViaturasBD();

        spViaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (viaturasDisp != null && !viaturasDisp.isEmpty()) {
                    idViaturaSelecionada = viaturasDisp.get(i).getId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fabReservar.setImageResource(R.drawable.ic_action_reservar);
        idBoleia = getIntent().getIntExtra(BOLEIA_ID, -1);
    }

    private void detalhesBoleiaFrament(){

        if (idBoleia != -1) {
            boleia = Singleton.getInstance(this).getBoleia(idBoleia);
            if (boleia != null) {
                if (condutorIsDono()) {
                    setInputsEnabled(true);
                    fabReservar.setVisibility(GONE);
                    fabGuardar.setImageResource(R.drawable.ic_action_guardar);
                    setFabGuardar();
                }else{
                    setInputsEnabled(false);
                    fabGuardar.setVisibility(GONE);
                    fabReservar.setImageResource(R.drawable.ic_action_reservar);
                    setFabReservar();
                }
                setTitle(getString(R.string.txt_detalhes) + " " + boleia.getOrigem() + "->" + boleia.getDestino());
                etOrigem.setText(boleia.getOrigem());
                etDestino.setText(boleia.getDestino());
                etData_hora.setText(boleia.getData_hora());
                etLugares_disponiveis.setText(String.valueOf(boleia.getLugares_disponiveis()));
                etPreco.setText(String.valueOf(boleia.getPreco()));

                for (int i = 0; i < viaturasDisp.size(); i++) {
                    if (viaturasDisp.get(i).getId() == boleia.getViatura_id()) {
                        spViaturas.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            setTitle(R.string.txt_adicionar_boleia);
            fabReservar.setVisibility(GONE);
            fabGuardar.setImageResource(R.drawable.ic_action_add);
            setFabGuardar();
        }
    }

    private boolean condutorIsDono() {

       Viatura viatura = Singleton.getInstance(this).getViatura(boleia.getViatura_id());

        if (viatura == null) {
            return false;
        }

        int perfilIdBoleia = viatura.getPerfil_id();
        return Integer.parseInt(perfil_id) == perfilIdBoleia;
    }

    private void setFabReservar() {
        fabReservar.setOnClickListener(v -> {
            {
                {
                    if (boleia != null) {
                        findViewById(R.id.mainContent).setVisibility(GONE);
                        fabReservar.setVisibility(GONE);

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
                }
            }
        });
    }

    private void setFabGuardar(){
        fabGuardar.setOnClickListener(v -> {
            if (isBoleiaValida()) {
                if (idBoleia == -1) {
                    Boleia novaBoleia = new Boleia(0,
                            etOrigem.getText().toString(),
                            etDestino.getText().toString(),
                            etData_hora.getText().toString(),
                            Integer.parseInt(etLugares_disponiveis.getText().toString()),
                            Double.parseDouble(etPreco.getText().toString()),
                            idViaturaSelecionada);
                    Singleton.getInstance(getApplicationContext()).adicionarBoleiaAPI(token, novaBoleia, getApplicationContext());
                } else {
                    boleia.setOrigem(etOrigem.getText().toString());
                    boleia.setDestino(etDestino.getText().toString());
                    boleia.setData_hora(etData_hora.getText().toString());
                    boleia.setLugares_disponiveis(Integer.parseInt(etLugares_disponiveis.getText().toString()));
                    boleia.setPreco(Double.parseDouble(etPreco.getText().toString()));
                    boleia.setViatura_id(idViaturaSelecionada);
                    Singleton.getInstance(getApplicationContext()).editarBoleiaAPI(token, boleia, getApplicationContext());
                }
            }
        });
    }
    private void showMaterialDateTimePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a Data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Selecione a Hora")
                    .build();

            timePicker.show(getSupportFragmentManager(), "TIME_PICKER");

            timePicker.addOnPositiveButtonClickListener(v -> {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                etData_hora.setText(sdf.format(calendar.getTime()));
            });
        });
    }
    private boolean isBoleiaValida() {
        if (etOrigem.getText().toString().isEmpty()) {
            etOrigem.setError("Origem obrigatória");
            return false;
        }
        if (etDestino.getText().toString().isEmpty()) {
            etDestino.setError("Destino obrigatório");
            return false;
        }
        if (etData_hora.getText().toString().isEmpty()) {
            etData_hora.setError("Data obrigatória");
            return false;
        }
        try {
            Integer.parseInt(etLugares_disponiveis.getText().toString());
            Double.parseDouble(etPreco.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        perfil_id = sharedPreferences.getString("perfil_id", null);
        condutor = sharedPreferences.getString("condutor", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (idBoleia != -1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_remover, menu);

            MenuItem itemRemover = menu.findItem(R.id.itemRemover);
            if (itemRemover != null && !"1".equals(condutor)) {
                itemRemover.setVisible(false);
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem itemRemover = menu.findItem(R.id.itemRemover);
            if (itemRemover != null) {
                if (boleia != null && "1".equals(condutor) && condutorIsDono()) {
                    itemRemover.setVisible(true);
                } else {
                    itemRemover.setVisible(false);
                }
            }
        } catch (Exception e) {
            MenuItem item = menu.findItem(R.id.itemRemover);
            if (item != null) item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if("1".equals(condutor)) {
            if (item.getItemId() == R.id.itemRemover) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.txt_remover_boleia)
                        .setIcon(android.R.drawable.ic_delete)
                        .setMessage(R.string.txt_tem_a_certeza_que_pretende_remover_a_boleia)
                        .setPositiveButton(getString(R.string.txt_sim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Singleton.getInstance(getApplicationContext()).removerBoleiaAPI(token, boleia, getApplicationContext());

                            }
                        })
                        .setNegativeButton(getString(R.string.txt_cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefreshDetalhes(int op) {
        Intent intent = new Intent();
        intent.putExtra("OPERACAO", op);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setInputsEnabled(boolean enabled) {
        View[] views = {etOrigem, etDestino, etData_hora, etLugares_disponiveis, etPreco, spViaturas};

        for (View v : views) {
            if (v != null) {
                v.setEnabled(enabled);

            }
        }
    }

    @Override
    public void onRefreshListaViaturas(ArrayList<Viatura> listaViaturas) {

        this.viaturasDisp = listaViaturas;
        detalhesBoleiaFrament();
        invalidateOptionsMenu();

        if ("1".equals(condutor) && (viaturasDisp == null || viaturasDisp.isEmpty())) {
            Toast.makeText(this, "Crie uma viatura primeiro!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayList<String> nomesViaturas = new ArrayList<>();
        for (Viatura v : viaturasDisp) {
            nomesViaturas.add(v.getMarca() + " [" + v.getModelo() + "]");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nomesViaturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spViaturas.setAdapter(adapter);


        if (boleia != null) {
            for (int i = 0; i < viaturasDisp.size(); i++) {
                if (viaturasDisp.get(i).getId() == boleia.getViatura_id()) {
                    spViaturas.setSelection(i);
                    idViaturaSelecionada = viaturasDisp.get(i).getId();
                    break;
                }
            }
        } else if (!viaturasDisp.isEmpty()) {
            idViaturaSelecionada = viaturasDisp.get(0).getId();
        }
    }

}
