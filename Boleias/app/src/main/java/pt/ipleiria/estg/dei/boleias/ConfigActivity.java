package pt.ipleiria.estg.dei.boleias;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class ConfigActivity extends AppCompatActivity {

    private Switch switchServer;
    private Button btnConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchServer = findViewById(R.id.switchServer);
        btnConfig = findViewById(R.id.btnConfig);

        SharedPreferences prefs = getSharedPreferences("CONFIGS", MODE_PRIVATE);
        String ipAtual = prefs.getString("IP_SERVIDOR", "192.168.42.110");

        if (ipAtual.equals("172.22.21.244")) {
            switchServer.setChecked(true);
        } else {
            switchServer.setChecked(false);
        }

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ipSelecionado;

                if (switchServer.isChecked()) {
                    ipSelecionado = "172.22.21.244";
                } else {
                    ipSelecionado = "192.168.42.110";
                }


                prefs.edit().putString("IP_SERVIDOR", ipSelecionado).apply();

                Singleton.getInstance(ConfigActivity.this).configurarUrls(ipSelecionado);

                Toast.makeText(ConfigActivity.this, "Configurado para: " + ipSelecionado, Toast.LENGTH_SHORT).show();

                finish();

            }
        });




    }
}