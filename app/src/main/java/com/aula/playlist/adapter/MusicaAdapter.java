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

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder>{

    private List<Musica> musicas;
    private OnMusicaClickListener listener;

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
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_musica, parent, false);

        return new MusicaViewHolder(card);
    }


    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = musicas.get(position);
        holder.bind(musica, listener);
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }
    public static class MusicaViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitulo, txtArtista, txtIndicadoPor;
        ImageButton btnVotar, btnExcluir;

        public MusicaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtArtista = itemView.findViewById(R.id.txtArtista);
            txtIndicadoPor = itemView.findViewById(R.id.txtIndicadoPor);
            btnVotar = itemView.findViewById(R.id.btnVotar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
        public void bind(Musica musica, OnMusicaClickListener listener){
            txtTitulo.setText(musica.getTitulo());
            txtArtista.setText(musica.getArtista());
            txtIndicadoPor.setText("Indicada por " + musica.getIndicadoPor());

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
    public void setOnMusicaClickListener(OnMusicaClickListener listener){
        this.listener = listener;
    }
}
