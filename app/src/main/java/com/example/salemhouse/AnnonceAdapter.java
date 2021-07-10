package com.example.salemhouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {
    private final List<Annonce> annonces;

    public AnnonceAdapter(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    @NonNull
    @NotNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AnnonceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_announce, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);
        String prix = annonce.getPrix() + " $/mois";
        String nbrePieces = annonce.getPieces() + " pces";
        String nbreChambres = annonce.getChambres() + " ch";
        String surface = annonce.getSurface() + " m²";
        String adresse = annonce.getAdresse();
        holder.prix.setText(prix);
        holder.pieces.setText(nbrePieces);
        holder.chambres.setText(nbreChambres);
        holder.surface.setText(surface);
        holder.adresse.setText(adresse);
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView prix;
        TextView pieces;
        TextView chambres;
        TextView surface;
        TextView adresse;

        public AnnonceViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            prix = itemView.findViewById(R.id.prix);
            pieces = itemView.findViewById(R.id.pieces);
            chambres = itemView.findViewById(R.id.chambres);
            surface = itemView.findViewById(R.id.surface);
            adresse = itemView.findViewById(R.id.adresse);
        }
    }
}
