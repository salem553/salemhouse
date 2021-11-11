package com.example.salemhouse;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.AnnonceViewHolder> {
    private final Context context;
    private final CollectionReference colReservations;
    private final List<Reservation> reservations;
    private final StorageReference refAnnonces;


    public ReservationAdapter(Context context, CollectionReference colReservations, List<Reservation> reservations, StorageReference refAnnonces) {
        this.context = context;
        this.colReservations = colReservations;
        this.reservations = reservations;
        this.refAnnonces = refAnnonces;
    }

    @NonNull
    @NotNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AnnonceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reservation, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull AnnonceViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        String prix = reservation.getAnnonce().getPrix() + " $/mois";
        String nbrePieces = reservation.getAnnonce().getPieces() + " pces";
        String nbreChambres = reservation.getAnnonce().getChambres() + " ch";
        String surface = reservation.getAnnonce().getSurface() + " m²";
        String adresse = reservation.getAnnonce().getAdresse();
        holder.prix.setText(prix);
        holder.pieces.setText(nbrePieces);
        holder.chambres.setText(nbreChambres);
        holder.surface.setText(surface);
        holder.adresse.setText(adresse);
        refAnnonces.child(reservation.getAnnonce().getId()).listAll().addOnCompleteListener(task -> {
           if(task.isSuccessful() && task.getResult() != null){
               List<StorageReference> items = task.getResult().getItems();
               if(items.size() > 0){
                   StorageReference image = items.get(0);
                   Log.e("REF",image.getPath());
                   Glide.with(context).load(image).into(holder.image);
               }
           }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,AnnonceActivity.class);
            intent.putExtra("id",reservation.getAnnonce().getId());
            context.startActivity(intent);
        });
        holder.annuler.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new MaterialAlertDialogBuilder(context);
            dialog.setMessage(R.string.message_confirmation)
                    .setNegativeButton("Annuler",(dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Confirmer",(dialogInterface, i) -> colReservations.document(reservation.getId()).delete().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            holder.annuler.setBackgroundResource(R.drawable.bg_bouton_retire);
                            holder.annuler.setText("Rétiré");
                            holder.annuler.setTextColor(context.getResources().getColor(R.color.black));
                            holder.annuler.setActivated(false);
                        }
                    }))
                    .setCancelable(true)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView prix;
        TextView pieces;
        TextView chambres;
        TextView surface;
        TextView adresse;
        Button annuler;

        public AnnonceViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            prix = itemView.findViewById(R.id.prix);
            pieces = itemView.findViewById(R.id.pieces);
            chambres = itemView.findViewById(R.id.chambres);
            surface = itemView.findViewById(R.id.surface);
            adresse = itemView.findViewById(R.id.adresse);
            annuler = itemView.findViewById(R.id.reserver);
        }
    }
}
