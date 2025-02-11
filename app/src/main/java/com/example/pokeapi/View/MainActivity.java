package com.example.pokeapi.View;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokeapi.Model.Pokemon;
import com.example.pokeapi.R;
import com.example.pokeapi.Model.PokemonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList;
    private EditText searchInput;
    private Button backButton;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.pokemonRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        backButton = findViewById(R.id.backButton);

        queue = Volley.newRequestQueue(this);
        pokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(this, pokemonList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchAllPokemon();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPokemon(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAllPokemon() {
        int totalPokemons = 1025; // Total de Pokémon disponibles
        for (int i = 0; i < totalPokemons / 20; i++) { // La API devuelve 20 por página, así que realizamos varias peticiones
            String url = "https://pokeapi.co/api/v2/pokemon?offset=" + (i * 20) + "&limit=20";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int j = 0; j < results.length(); j++) {
                                JSONObject obj = results.getJSONObject(j);
                                String name = obj.getString("name");
                                String detailsUrl = obj.getString("url");
                                fetchPokemonDetails(name, detailsUrl);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(MainActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show());
            queue.add(request);
        }
    }


    private void fetchPokemonDetails(String name, String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int id = response.getInt("id");
                        String spriteUrl = response.getJSONObject("sprites").getString("front_default");
                        String cryUrl = response.getJSONObject("cries").getString("latest");

                        // Obtener los tipos del Pokémon
                        JSONArray typesArray = response.getJSONArray("types");
                        StringBuilder types = new StringBuilder();

                        for (int i = 0; i < typesArray.length(); i++) {
                            JSONObject typeObject = typesArray.getJSONObject(i);
                            String type = typeObject.getJSONObject("type").getString("name");
                            if (i != 0) {
                                types.append(", ");
                            }
                            types.append(type);
                        }

                        // Crear un objeto Pokemon con el tipo
                        String pokemonTypes = types.toString();
                        pokemonList.add(new Pokemon(id, name, spriteUrl, cryUrl, pokemonTypes));

                         // Esto nos ordenara los pokemon por orden
                        Collections.sort(pokemonList, new Comparator<Pokemon>() {
                            @Override
                            public int compare(Pokemon p1, Pokemon p2) {
                                return Integer.compare(p1.getNumber(), p2.getNumber());
                            }
                        });


                        // Notificar al adaptador
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Error al obtener detalles", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }



    private void filterPokemon(String query) {
        List<Pokemon> filteredList = new ArrayList<>();
        for (Pokemon p : pokemonList) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(p);
            }
        }
        adapter.updateList(filteredList);
    }
}
