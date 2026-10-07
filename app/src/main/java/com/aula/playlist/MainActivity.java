package com.aula.playlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.playlist.adapter.MusicaAdapter;
import com.aula.playlist.model.Musica;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaClickListener {

    private String meuNome;

    private RecyclerView listaMusicas;

    MusicaAdapter adapter;
    private List<Musica> musicas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Carregar dados do usuario
        carregarNome();

        // Ação do botão de adicionar
        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> mostrarDialogoNovaMusica());


        // Adicionar SUA IMPLEMENTAÇÃO AQUI

        //Configurar a implementação do adapter
        adapter = new MusicaAdapter(musicas);

        //Configurar OnClickMusica
        adapter.setOnMusicaClickListener(this);

        //Configurar recyclerView
        listaMusicas = findViewById(R.id.listaMusicas);
        listaMusicas.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        listaMusicas.setAdapter(adapter);

        db.collection("playlist")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null){
                        Toast.makeText(this, "Erro ao carregar músicas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    musicas.clear();
                    for (var documento : snapshot.getDocuments()){
                        Musica musica = documento.toObject(Musica.class);
                        musica.setId(documento.getId());
                        musicas.add(musica);
                    }
                    adapter.notifyDataSetChanged();
                });




    }

    //Configurar firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    private void carregarNome() {
        SharedPreferences localNome = getSharedPreferences("nomeUsuario", MODE_PRIVATE);
        meuNome = localNome.getString("nomeUsuario", null);

        if (meuNome != null && !meuNome.equals("")) {
            Toast.makeText(this, "Bem-vindo(a) " + meuNome, Toast.LENGTH_LONG).show();
            return;
        }

        // Ativar a caixa de texto para digitar o nome
        EditText campo = new EditText(this);
        campo.setHint("Seu nome");

        new AlertDialog.Builder(this)
                .setTitle("Quem é você")
                .setMessage("Vai aparecer ao lado das músicas que voce indicar.")
                .setView(campo)
                .setCancelable(false)
                .setPositiveButton("Pronto",(dialogInterface, i) -> {
                    meuNome = campo.getText().toString();
                    localNome.edit().putString("nomeUsuario", meuNome).apply();

                }).show();

    }


    private void mostrarDialogoNovaMusica() {
        View form = LayoutInflater.from(this).inflate(R.layout.tela_musica, null);
        EditText campoTitulo = form.findViewById(R.id.campoTitulo);
        EditText campoArtista = form.findViewById(R.id.campoArtista);

        new AlertDialog.Builder(this)
                .setTitle("Indicar música")
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Adicionar", (d, w) -> {
                    String titulo = campoTitulo.getText().toString().trim();
                    String artista = campoArtista.getText().toString().trim();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(artista)) {
                        Toast.makeText(this, "Preencha título e artista", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Musica musica = new Musica(titulo,artista,meuNome);
                    musicas.clear();
                    db.collection("playlist")
                            .add(musica)
                            .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Música adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                        campoTitulo.setText("");
                                        campoArtista.setText("");
                                        campoTitulo.requestFocus();
                                    }
                            )
                            .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erro ao adicionar música", Toast.LENGTH_SHORT).show();
                                    });

                })
                .show();
    }

    public void votarMusica(Musica musica){
        db.collection("playlist")
                .document(musica.getId())
                .update("votos", FieldValue.increment(1));
    }
    public void excluirMusica(Musica musica){}

}