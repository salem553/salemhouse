package com.example.salemhouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AnnonceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);
        TextView prixTV = findViewById(R.id.prix);
        TextView piecesTV = findViewById(R.id.pieces);
        TextView chambresTV = findViewById(R.id.chambres);
        TextView surfaceTV = findViewById(R.id.surface);
        TextView adresseTV = findViewById(R.id.adresse);
        TextView garantieTV = findViewById(R.id.garantie);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        CollectionReference colAnnonces = FirebaseFirestore.getInstance().collection("annonces");
        colAnnonces.document(id).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Annonce annonce = task.getResult().toObject(Annonce.class);
                if(annonce != null){
                    String prix = annonce.getPrix() + " $/mois";
                    String nbrePieces = annonce.getPieces() + " pces";
                    String nbreChambres = annonce.getChambres() + " ch";
                    String surface = annonce.getSurface() + " m²";
                    String adresse = annonce.getAdresse();
                    String garantie = annonce.getPrix() * 3 + " $";
                    prixTV.setText(prix);
                    piecesTV.setText(nbrePieces);
                    chambresTV.setText(nbreChambres);
                    surfaceTV.setText(surface);
                    adresseTV.setText(adresse);
                    garantieTV.setText(garantie);
                }
            }
        });
    }
}