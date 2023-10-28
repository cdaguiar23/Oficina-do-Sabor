package com.cdaguiar.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.cdaguiar.instagram.R;
import com.cdaguiar.instagram.helper.ConfiguracaoFirebase;
import com.cdaguiar.instagram.helper.UsuarioFirebase;
import com.cdaguiar.instagram.model.Postagem;
import com.cdaguiar.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }
    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private Usuario usuarioLogado;
    private Dialog dialog;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        // Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Recuperar os dados para uma nova postagem
        recuperarDadosPostagem();

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recupera a imagem escolhid apelo usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            //        imagemFiltro = imagem.copy(imagem.getConfig(), true);


//            imagemFiltro.copy(imagem.getConfig(), true);
//            Filter filter = FilterPack.getAdeleFilter(getApplicationContext());
//            imageFotoEscolhida.setImageBitmap(filter.processFilter(imagemFiltro));
        }
    }

    private void abrirDialogCarregamento(String titulo) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        alert.show();
    }

    private void recuperarDadosPostagem() {
        abrirDialogCarregamento("Carregando dados, aguarde");
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Recupera dados do usuário logado
                usuarioLogado = snapshot.getValue(Usuario.class);

                //Recuperar seguidores
                DatabaseReference seguidoresRef = firebaseRef.child("seguidores").child(idUsuarioLogado);
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        seguidoresSnapshot = snapshot;
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                    publicarPostagem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void publicarPostagem() {

        abrirDialogCarregamento("Salvando postagem");
        final Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(textDescricaoFiltro.getText().toString());

        // Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        // salvar imagem no Firebase Storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imagemRef = storageRef.child("imagens").child("postagens").child(postagem.getId() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this, "Erro ao salvar a postagem, tente novamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        // Recuperar a url da foto
                        Uri url = task.getResult();
                        postagem.setCaminhoFoto(url.toString());

                        // Atualizar a quantidade de postagens
                        int qtdPostagem = usuarioLogado.getPostagens() + 1;
                        usuarioLogado.setPostagens(qtdPostagem);
                        usuarioLogado.atualizarQtdPostagem();

                        // Salvar postagem
                        if (postagem.salvar(seguidoresSnapshot)) {
                            Toast.makeText(FiltroActivity.this, "Sucesso ao salvar a postagem", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            finish();
                        }
                    }
                });

            }
        });
    }

}