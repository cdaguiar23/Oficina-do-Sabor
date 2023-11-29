package com.cdaguiar.oficinadosabor.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cdaguiar.oficinadosabor.R;
import com.cdaguiar.oficinadosabor.activity.EditarPerfilActivity;
import com.cdaguiar.oficinadosabor.adapter.AdapterGrid;
import com.cdaguiar.oficinadosabor.helper.ConfiguracaoFirebase;
import com.cdaguiar.oficinadosabor.helper.UsuarioFirebase;
import com.cdaguiar.oficinadosabor.model.Postagem;
import com.cdaguiar.oficinadosabor.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo, textViewNome, textViewEndereco, textViewBairro, textViewCidade, textViewEstado, textViewEmail, textViewTelefone;
    private Button buttonAcaoPerfil;
    private Usuario usuarioLogado;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference firebaseRef;
    private DatabaseReference postagensUsuarioRef;
    private AdapterGrid adapterGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadoUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        // Configurar referência postagens usuário
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase().child("postagens").child(usuarioLogado.getId())
;
        // Configurações dos componenetes
        inicializarComponentes(view);

        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        // Inicializa Image Loader
        incializarImageLoader();

        //Carrega as fotos das postagens de um usuário
//        carregarFotosPostagem();

        return  view;
    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                String postagens = String.valueOf(usuario.getPostagens());
                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());

                // configura valores recuperados
                textPublicacoes.setText(postagens);
                textSeguidores.setText(seguidores);
                textSeguindo.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void  carregarFotosPostagem() {
        // Recupera as fotos postadas pelo usuário
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Configura o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                // Configurar adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Instancia a UniveralImageLoader
    private void incializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder( getActivity() )
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );
    }

    private void inicializarComponentes(View view) {
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imageEditarPerfil);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        textViewNome = view.findViewById(R.id.textViewNome);
        textViewEndereco = view.findViewById(R.id.textViewEndereco);
        textViewBairro = view.findViewById(R.id.textViewBairro);
        textViewCidade = view.findViewById(R.id.textViewCidade);
        textViewEstado = view.findViewById(R.id.textViewEstado);
        textViewTelefone = view.findViewById(R.id.textViewTelefone);
        textViewEmail = view.findViewById(R.id.textViewEmail);


    }

    @Override
    public void onStart() {
        super.onStart();
        //Recuperar foto do usuário
        recuperarFotoUsuario();

        // Recueprar dados do usuário logado
//        recuperarDadosUsuarioLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
//        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }

    private  void recuperarFotoUsuario() {
        usuarioLogado = UsuarioFirebase.getDadoUsuarioLogado();

        // Recupera foto do suuário
        String caminhoFoto = usuarioLogado.getCaminhoFoto();;
        if (caminhoFoto != null) {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }
    }

    private void exibirInformacoesUsuario(Usuario usuario) {

        // Verifica se o usuário tem informações de nome, e-mail e endereço
        if (usuario.getNome() != null) {
            textViewNome.setText("Nome: " + usuario.getNome());
        }

        if (usuario.getEmail() != null) {
            textViewEmail.setText("E-mail: " + usuario.getEmail());
        }

        // Recupera informações adicionais do usuário
        DatabaseReference usuarioInfoRef = ConfiguracaoFirebase.getFirebase().child("informacoes_usuarios").child(usuario.getId());
        usuarioInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Se as informações adicionais do usuário existirem no Firebase, atualize os TextViews
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if (usuario.getEndereco() != null) {
                        textViewEndereco.setText("Endereço: " + usuario.getEndereco());
                    }

                    if (usuario.getBairro() != null) {
                        textViewBairro.setText("Bairro: " + usuario.getBairro());
                    }

                    if (usuario.getCidade() != null) {
                        textViewCidade.setText("Cidade: " + usuario.getCidade());
                    }

                    if (usuario.getEstado() != null) {
                        textViewEstado.setText("Estado: " + usuario.getEstado());
                    }

                    if (usuario.getTelefone() != null) {
                        textViewTelefone.setText("Telefone: " + usuario.getTelefone());
                    }

                    if (usuario.getEmail() != null) {
                        textViewTelefone.setText("Telefone: " + usuario.getEmail());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Trate o erro, se necessário
            }
        });
    }

}