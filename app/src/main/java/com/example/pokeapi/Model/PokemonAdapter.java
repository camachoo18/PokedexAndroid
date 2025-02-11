package com.example.pokeapi.Model;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.pokeapi.R;

import java.io.IOException;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {
    private Context context;
    private List<Pokemon> pokemonList;

    public PokemonAdapter(Context context, List<Pokemon> pokemonList) {
        this.context = context;
        this.pokemonList = pokemonList;
    }

    public void updateList(List<Pokemon> newList) {
        pokemonList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pokemon_item, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);
        holder.pokemonName.setText(pokemon.getName());
        holder.pokemonNumber.setText("#" + pokemon.getNumber());
        holder.pokemonType.setText(pokemon.getType());

        Glide.with(context).load(pokemon.getImageUrl()).into(holder.pokemonImage);

        holder.soundButton.setOnClickListener(v -> {
            String soundUrl = pokemon.getSoundUrl();  // Aquí obtienes la URL del sonido
            fetchSoundAndPlay(soundUrl);
        });
    }

    private void fetchSoundAndPlay(String soundUrl) {

        StringRequest soundRequest = new StringRequest(com.android.volley.Request.Method.GET, soundUrl,
                response -> {
                    playSound(soundUrl);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Error al obtener el sonido", Toast.LENGTH_SHORT).show();
                });


        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(soundRequest);
    }

    private void playSound(String soundUrl) {
        try {

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(soundUrl);  // Usar la URL del sonido obtenida
            mediaPlayer.prepareAsync();  // Preparar el MediaPlayer de forma asincrónica


            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al cargar el sonido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonName, pokemonNumber, pokemonType;
        ImageView pokemonImage;
        Button soundButton;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonName = itemView.findViewById(R.id.pokemonName);
            pokemonNumber = itemView.findViewById(R.id.pokemonNumber);
            pokemonType = itemView.findViewById(R.id.pokemonType);
            pokemonImage = itemView.findViewById(R.id.pokemonImage);
            soundButton = itemView.findViewById(R.id.soundButton);
        }
    }
}
