package com.example.salemhouse;

import com.google.firebase.firestore.GeoPoint;

public class Annonce {
    private String id;
    double prix;
    int pieces;
    int chambres;
    int surface;
    String adresse;
    GeoPoint localisation;

    public Annonce(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrix() {
        return prix;
    }

    public int getPieces() {
        return pieces;
    }

    public int getChambres() {
        return chambres;
    }

    public int getSurface() {
        return surface;
    }

    public String getAdresse() {
        return adresse;
    }

    public GeoPoint getLocalisation() {
        return localisation;
    }
}
