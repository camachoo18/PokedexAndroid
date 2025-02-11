package com.example.pokeapi.Controller;

import com.example.pokeapi.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonController extends AppCompatActivity {

    EditText searchInput;
    Button searchButton;
    ImageView pokemonImage;
    TextView pokemonNumber, pokemonName, pokemonType;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        searchInput = (EditText) findViewById(R.id.searchInput);
        searchButton = (Button) findViewById(R.id.searchInput);
        pokemonImage =(ImageView) findViewById(R.id.pokemonImage);
        pokemonNumber = (TextView) findViewById(R.id.pokemonNumber);
        pokemonName =(TextView) findViewById(R.id.pokemonName);
        pokemonType = (TextView) findViewById(R.id.pokemonType);

        queue = Volley.newRequestQueue(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchButton.setOnClickListener(v -> {
            String pokemonNameInput = searchInput.getText().toString().trim().toLowerCase();

            if (!pokemonNameInput.isEmpty()) {
                fetchPokemonData(pokemonNameInput);
            } else {
                Toast.makeText(PokemonController.this, "Ingrese un nombre de Pokémon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPokemonData(String pokemonNameInput) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonNameInput;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jobj = new JSONObject(response);

                        // Obtener nombre y número
                        String name = jobj.getString("name");
                        int number = jobj.getInt("id");

                        // Obtener imagen
                        String spriteUrl = jobj.getJSONObject("sprites").getString("front_default");

                        // Obtener el tipo
                        JSONArray typesArray = jobj.getJSONArray("types");
                        String typeName = typesArray.getJSONObject(0).getJSONObject("type").getString("name");

                        // Actualizar UI sin capitalizar
                        pokemonName.setText(name);
                        pokemonNumber.setText("#" + number);
                        pokemonType.setText(typeName);

                        // Cargar imagen con Glide
                        Glide.with(PokemonController.this).load(spriteUrl).into(pokemonImage);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PokemonController.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(PokemonController.this, "Pokémon no encontrado", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }
}
