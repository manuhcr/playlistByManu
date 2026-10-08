package com.aula.playlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.playlist.R;
import com.aula.playlist.model.Musica;

import java.util.List;

// Classe MusicaAdapter.java usada para exibir as músicas no RecyclerView.
public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder>{

    // Lista de músicas recebida do Firestore para exibição no RecyclerView.
    private List<Musica> musicas;

    // Listener para eventos de clique na música.
    private OnMusicaClickListener listener;

    // Interface usada para fazer um contrato entre a classe MusicaAdapter e a classe
    // MainActivity. Esse contrato determina que a classe MainActivity deve implementar
    // os métodos votarMusica e excluirMusica.
    public interface OnMusicaClickListener{
        void votarMusica(Musica musica);
        void excluirMusica(Musica musica);
    }

    public MusicaAdapter(List <Musica> musicas){
        this.musicas = musicas;
    }
    @NonNull
    @Override

    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar o layout do item da música e criar um ViewHolder para ele.
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_musica, parent, false);

        return new MusicaViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = musicas.get(position);
        // Chamar o método bind para exibir os dados da música
        // e configurar os eventos do clique nos botões de votar e excluir.
        holder.bind(musica, listener);
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitulo, txtArtista, txtIndicadoPor, txtVotos;
        ImageButton btnVotar, btnExcluir;

        public MusicaViewHolder(@NonNull View itemView) {

            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtArtista = itemView.findViewById(R.id.txtArtista);
            txtIndicadoPor = itemView.findViewById(R.id.txtIndicadoPor);
            txtVotos = itemView.findViewById(R.id.txtVotos);
            btnVotar = itemView.findViewById(R.id.btnVotar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }

        public void bind(Musica musica, OnMusicaClickListener listener){
            txtTitulo.setText(musica.getTitulo());
            txtArtista.setText(musica.getArtista());
            txtIndicadoPor.setText("Indicada por " + musica.getIndicadoPor());
            txtVotos.setText(String.valueOf(musica.getVotos()));

            btnVotar.setOnClickListener(v -> {
                if (listener != null){
                    listener.votarMusica(musica);
                }

            });
            btnExcluir.setOnClickListener(v -> {
                if (listener != null){
                    listener.excluirMusica(musica);
                }

            });



        }
    }

    // Define o listener responsável pelos eventos de clique.
    public void setOnMusicaClickListener(OnMusicaClickListener listener){
        this.listener = listener;
    }
}
