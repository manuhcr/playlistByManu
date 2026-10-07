package com.aula.playlist.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Musica {
    //ATENÇÃO: liberar comentario apos implementar o firebase

    @DocumentId
    private String id;

    private String titulo;
    private String artista;
    private String indicadoPor;
    private long votos;

    //ATENÇÃO: liberar comentario apos implementar o firebase
    @ServerTimestamp
    private Date criadoEm;

    public Musica() {
    }

    public Musica(String id, String titulo, String artista, String indicadoPor, long votos, Date criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.indicadoPor = indicadoPor;
        this.votos = votos;
        this.criadoEm = criadoEm;
    }

    public Musica(String titulo, String artista, String indicadoPor) {
        this.titulo = titulo;
        this.artista = artista;
        this.indicadoPor = indicadoPor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getIndicadoPor() {
        return indicadoPor;
    }

    public void setIndicadoPor(String indicadoPor) {
        this.indicadoPor = indicadoPor;
    }

    public long getVotos() {
        return votos;
    }

    public void setVotos(long votos) {
        this.votos = votos;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }
}
