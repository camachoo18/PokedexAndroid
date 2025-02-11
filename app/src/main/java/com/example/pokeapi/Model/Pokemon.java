package com.example.pokeapi.Model;

public class Pokemon {
    private int id;
    private String name;
    private String imageUrl;
    private String soundUrl;

    private String type;

    // Constructor
    public Pokemon(int id, String name, String imageUrl, String soundUrl, String type) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.soundUrl = soundUrl;
        this.type = type;
    }


    // Getters
    public String getName() {
        return name;
    }

    public int getNumber() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }
}

