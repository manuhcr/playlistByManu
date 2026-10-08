package com.aula.playlist.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
// Classe Musica.java usada para armazenar as músicas e seus dados.
public class Musica {

    // Identificador do documento da música armazenado no Firestore.
    @DocumentId
    private String id;
    // Dados da música armazenados no Firestore.
    private String titulo;
    private String artista;
    private String indicadoPor;
    private long votos;
    @ServerTimestamp
    private Date criadoEm;

    // Construtor padrão da classe Musica.
    public Musica() {
    }
    // Construtor da classe Música que recebe os dados da música que será usado
    // em MainActivity para criar um documento no Firestore.
    public Musica(String titulo, String artista, String indicadoPor) {
        this.titulo = titulo;
        this.artista = artista;
        this.indicadoPor = indicadoPor;
    }

    // Getters e setters dos atributos da classe Musica.
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
