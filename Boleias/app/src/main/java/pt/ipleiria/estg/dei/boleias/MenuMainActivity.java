package pt.ipleiria.estg.dei.boleias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MenuMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String NOME = "nome";
    public static final String TOKEN = "token";
    public static final String CONDUTOR = "condutor";
    public static final String PERFIL_ID = "perfil_id";

    public static final int ADD = 100;
    public static final int EDIT = 200;
    public static final int DEL = 300;


    private String nome;
    private String token;
    private String condutor;
    private String perfil_id;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.ndOpen, R.string.ndClose);

        toggle.syncState();
        drawer.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        carregarCabecalho();
        carregarFragmentoInicial();

    }

    private void carregarFragmentoInicial() {

        int lastId = sharedPreferences.getInt("LAST_FRAGMENT", R.id.navBoleias);

        MenuItem lastItem = navigationView.getMenu().findItem(lastId);

        if (lastItem != null) {
            openFragmentById(lastId, lastItem.getTitle().toString());
            navigationView.setCheckedItem(lastId);
        }else {
            openFragmentById(R.id.navBoleias, "Boleias");
            navigationView.setCheckedItem(R.id.navBoleias);
        }

    }


    private void carregarCabecalho() {
        sharedPreferences = getSharedPreferences("DADOS_USER", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String intentToken = getIntent().getStringExtra(TOKEN);

        if (intentToken != null) {
            nome = getIntent().getStringExtra(NOME);
            token = intentToken;
            condutor = String.valueOf(getIntent().getIntExtra(CONDUTOR, 0));
            perfil_id = String.valueOf(getIntent().getIntExtra(PERFIL_ID, 0));

            editor.putString(NOME, nome);
            editor.putString(TOKEN, token);
            editor.putString(CONDUTOR, condutor);
            editor.putString(PERFIL_ID, perfil_id);
            editor.apply();

        } else {
            nome = sharedPreferences.getString(NOME, "Sem nome");
            token = sharedPreferences.getString(TOKEN, null);
            condutor = sharedPreferences.getString(CONDUTOR, "0");
            perfil_id = sharedPreferences.getString(PERFIL_ID, null);
        }

        navigationView.getMenu().clear();
        if ("1".equals(condutor)) {
            navigationView.inflateMenu(R.menu.menu_main_condutor);
        } else {
            navigationView.inflateMenu(R.menu.menu_main_passageiro);
        }

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView nav_tvNome = headerView.findViewById(R.id.tvNome);
            nav_tvNome.setText(nome);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.navLogout) {
            efetuarLogout();
        } else {
        editor.putInt("LAST_FRAGMENT", id);
        editor.apply();
        openFragmentById(id, item.getTitle().toString());
    }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void efetuarLogout() {

        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openFragmentById(int id, String title) {
        Fragment fragment = null;

        if (id == R.id.navBoleias) {

            fragment = new ListaBoleiasFragment();

        }
        else if (id == R.id.navViaturas) {
            fragment = new ListaViaturasFragment();
        }
        else if (id == R.id.navReservas) {
            fragment = new ListaReservasFragment();
        }
        else if (id == R.id.navWishlist) {
            fragment = new ListaDestinosFavoritosFragment();
        }

        if (fragment != null) {
            setTitle(title);
            fragmentManager.beginTransaction()
                    .replace(R.id.contentFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}