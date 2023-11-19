package com.cdaguiar.oficinadosabor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cdaguiar.oficinadosabor.R;
import com.cdaguiar.oficinadosabor.fragment.AcessoAdministradorFragment;
import com.cdaguiar.oficinadosabor.fragment.FeedFragment;
import com.cdaguiar.oficinadosabor.fragment.PerfilFragment;
import com.cdaguiar.oficinadosabor.fragment.PesquisaFragment;
import com.cdaguiar.oficinadosabor.fragment.PostagemFragment;
import com.cdaguiar.oficinadosabor.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Oficina do Sabor" + "</font>"));
        setSupportActionBar(toolbar);

        // Configurações de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Configurar ButtonNavigatinView
        configuraBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

    }

    // Método responsável por criar a BottonNavigationView
    private void configuraBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);


        // Habilitar navegação
        habilitarNavegacao(bottomNavigationView);

        // Configura item selecionado inicialmente
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

    }

    // Método responsável por tratar eventos de clique na BottomNavigationView
    private void habilitarNavegacao(BottomNavigationView viewEx) {
        viewEx.setOnNavigationItemSelectedListener(item -> {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.produtos:
                    fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                    return true;
                case R.id.carrinho_compras:
                    fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                    return true;
                case R.id.busca_produtos:
                    fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                    return true;
                case R.id.acesso_administrador:
                    fragmentTransaction.replace(R.id.viewPager, new AcessoAdministradorFragment()).commit();
                    return true;
                case R.id.perfil_usuario:
                    fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}