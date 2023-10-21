package com.cdaguiar.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cdaguiar.instagram.R;
import com.cdaguiar.instagram.fragment.FeedFragment;
import com.cdaguiar.instagram.fragment.PerfilFragment;
import com.cdaguiar.instagram.fragment.PesquisaFragment;
import com.cdaguiar.instagram.fragment.PostagemFragment;
import com.cdaguiar.instagram.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Instagram");
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
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        // Faz as configurações iniciais do BottonNavigation
//        bottomNavigationViewEx.enableAnimation(true);
//        bottomNavigationViewEx.enableItemShiftingMode(false);
//        bottomNavigationViewEx.enableShiftingMode(false);
//        bottomNavigationViewEx.setTextVisibility(true);

        // Habilitar navegação
        habilitarNavegacao(bottomNavigationViewEx);

        // Configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

    }

    // Método responsável por tratar eventos de clique na BottomNavigationView
    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(item -> {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.ic_home:
                    fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                    return true;
                case R.id.ic_pesquisa:
                    fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                    return true;
                case R.id.ic_postagem:
                    fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                    return true;
                case R.id.ic_perfil:
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