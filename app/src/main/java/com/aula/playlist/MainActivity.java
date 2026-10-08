package com.aula.playlist;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaClickListener {

    private String meuNome;

    private RecyclerView listaMusicas;

    private TextView txtVazio;

    private MusicaAdapter adapter;
    private List<Musica> musicas = new ArrayList<>();

    // Instância do Firestore usada para acessar a coleção playlist.
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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


        // Adapter exibe a lista e envia os cliques de votar/excluir para a Activity.
        adapter = new MusicaAdapter(musicas);
        adapter.setOnMusicaClickListener(this);


        listaMusicas = findViewById(R.id.listaMusicas);
        listaMusicas.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        listaMusicas.setAdapter(adapter);

        //Configurar TextView vazio
        txtVazio = findViewById(R.id.txtVazio);

        // Observa a coleção "playlist" do Firestore em tempo real e mantém a
        // lista ordenada pelos votos.
        db.collection("playlist")
                .orderBy("votos", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null){
                        Toast.makeText(this, "Erro ao carregar músicas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    musicas.clear();
                    for (var documento : snapshot.getDocuments()){
                        Musica musica = documento.toObject(Musica.class);
                        // Converte cada documento em Musica e guarda seu ID para futuras alterações.
                        musica.setId(documento.getId());
                        musicas.add(musica);
                    }
                    // Mostra a mensagem de playlist vazia quando não há músicas.
                    if (musicas.isEmpty()){
                        listaMusicas.setVisibility(View.GONE);
                        txtVazio.setVisibility(View.VISIBLE);
                    }else {
                        listaMusicas.setVisibility(View.VISIBLE);
                        txtVazio.setVisibility(View.GONE);
                    }
                    // Atualiza o RecyclerView após alterar a lista de músicas.
                    adapter.notifyDataSetChanged();
                });






    }





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
                        Toast.makeText(this, "Preencha título e artista",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cria a música com o usuário que fez a indicação.
                    Musica musica = new Musica(titulo,artista,meuNome);
                    db.collection("playlist")
                            .add(musica)
                            .addOnSuccessListener(documentReference -> {
                                        campoTitulo.setText("");
                                        campoArtista.setText("");
                                        campoTitulo.requestFocus();
                                    }
                            )
                            .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erro ao adicionar música",
                                                Toast.LENGTH_SHORT).show();
                                    });

                })
                .show();
    }

    public void votarMusica(Musica musica){
        // Incrementa em 1 o número de votos da música no Firestore.
        db.collection("playlist")
                .document(musica.getId())
                .update("votos", FieldValue.increment(1));
    }
    public void excluirMusica(Musica musica){
        // Confirmar a exclusão se a música foi indicada por mim
        if (musica.getIndicadoPor().equals(meuNome)){
            new AlertDialog.Builder(this)
                    .setTitle("Excluir")
                    .setMessage("Tirar \"" + musica.getTitulo() + "\" da playlist?")
                    .setNegativeButton("Não", null)
                    .setPositiveButton("Excluir", (d, w) -> {
                        db.collection("playlist")
                                .document(musica.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(listaMusicas, "Música removida", Snackbar.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erro ao excluir música", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .show();
            }else {
            // Informar que a música não foi indicada por mim
            Toast.makeText(this, "Essa música é de " + musica.getIndicadoPor() + ".", Toast.LENGTH_SHORT).show();
        }

    }

}