package pt.ipleiria.estg.dei.boleias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import
        androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ipleiria.estg.dei.boleias.listeners.LoginListener;
import pt.ipleiria.estg.dei.boleias.modelos.Singleton;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private FloatingActionButton fabConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        fabConfig = findViewById(R.id.fabConfig);

        Singleton.getInstance(getApplicationContext()).setLoginListener(this);


        fabConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isUsernameValido(etUsername.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Email inválido!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordValido(etPassword.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Password inválida!!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    return;
                }

                Singleton.getInstance(getApplicationContext()).loginAPI(etUsername.getText().toString(), etPassword.getText().toString(), getApplicationContext());

            }
        });

    }




    private boolean isUsernameValido(String username)
    {
        if (username == null || username.isEmpty()) {

            return false;

        } else if (username.length() < 5){

            return false;

        } else {
            return true;
        }
    }

    private boolean isPasswordValido(String password)
    {
        if (password == null || password.isEmpty()) {

            return false;

        } else if (password.length() < 5){

            return false;

        } else {
            return true;
        }
    }


    @Override
    public void onValidateLogin(String nome, String token, int condutor, int perfil_id) {

        if(token != null) {
            Intent intent = new Intent(getApplicationContext(), MenuMainActivity.class);
            intent.putExtra(MenuMainActivity.NOME, nome);
            intent.putExtra(MenuMainActivity.TOKEN, token);
            intent.putExtra(MenuMainActivity.CONDUTOR, condutor);
            intent.putExtra(MenuMainActivity.PERFIL_ID, perfil_id);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, R.string.erro_credenciais, Toast.LENGTH_SHORT).show();
        }
    }

}