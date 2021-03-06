package com.example.salemhouse;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {
    private final Context context;
    private final List<Annonce> annonces;
    private final StorageReference refAnnonces;

    public AnnonceAdapter(Context context, List<Annonce> annonces, StorageReference refAnnonces) {
        this.context = context;
        this.annonces = annonces;
        this.refAnnonces = refAnnonces;
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
        refAnnonces.child(annonce.getId()).listAll().addOnCompleteListener(task -> {
           if(task.isSuccessful() && task.getResult() != null){
               List<StorageReference> items = task.getResult().getItems();
               if(items.size() > 0){
                   StorageReference image = items.get(0);
                   Log.e("REF",image.getPath());
                   Glide.with(context).load(image).into(holder.image);
               }
           }
        });
        holder.image.setOnClickListener(v -> {
            Intent intent = new Intent(context,AnnonceActivity.class);
            intent.putExtra("id",annonce.getId());
            context.startActivity(intent);
        });
        holder.reserver.setOnClickListener(v -> {
            Intent intent = new Intent(context,ReservationActivity.class);
            intent.putExtra("annonce",new Gson().toJson(annonce));
            context.startActivity(intent);
        });
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
        Button reserver;

        public AnnonceViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            prix = itemView.findViewById(R.id.prix);
            pieces = itemView.findViewById(R.id.pieces);
            chambres = itemView.findViewById(R.id.chambres);
            surface = itemView.findViewById(R.id.surface);
            adresse = itemView.findViewById(R.id.adresse);
            reserver = itemView.findViewById(R.id.reserver);
        }
    }
}
