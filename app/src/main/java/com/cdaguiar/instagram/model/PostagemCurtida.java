package com.cdaguiar.instagram.model;

import com.cdaguiar.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    public Feed feed;
    public Usuario usuario;
    public int qtdCurtidas = 0;

    public PostagemCurtida() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Objeto usuario
        HashMap<String, Object> dadosUsuario=  new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidasref = firebaseRef.child("postagens-curtidas").child(feed.getId()).child(usuario.getId()); // id_postagem e id_usuario_logado
        pCurtidasref.setValue(dadosUsuario);

        // Atualizar quantidadede curtidas
        atualizarQtd(1);
    }

    public void atualizarQtd(int valor) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pCurtidasref = firebaseRef.child("postagens-curtidas").child(feed.getId()).child("qtdCurtidas"); // id_postagem e id_usuario_logado
        setQtdCurtidas(getQtdCurtidas() + valor);
        pCurtidasref.setValue(getQtdCurtidas());
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
