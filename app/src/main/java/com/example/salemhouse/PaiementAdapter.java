package com.example.salemhouse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class PaiementAdapter extends RecyclerView.Adapter<PaiementAdapter.PaiementAdapterViewHolder> {
    private final Activity context;
    private final Annonce annonce;
    private final Resources resources;
    private final FirebaseUser firebaseUser;
    private final int[] images;
    private final AppCompatEditText nomComplet;
    private final AppCompatEditText emailTelephone;

    public PaiementAdapter(Activity context, Annonce annonce, FirebaseUser firebaseUser, int[] images, AppCompatEditText nomComplet, AppCompatEditText emailTelephone) {
        this.context = context;
        this.annonce = annonce;
        this.resources = context.getResources();
        this.firebaseUser = firebaseUser;
        this.images = images;
        this.nomComplet = nomComplet;
        this.emailTelephone = emailTelephone;
    }

    @NonNull
    @NotNull
    @Override
    public PaiementAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new PaiementAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_methode_paiement, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull @NotNull PaiementAdapterViewHolder holder, int position) {
        holder.methodePaiement.setImageDrawable(resources.getDrawable(images[position]));
        holder.methodePaiement.setOnClickListener(v -> {
            if(nomComplet.getText() != null && emailTelephone.getText() != null){
                String nom = nomComplet.getText().toString();
                String contact = emailTelephone.getText().toString();
                if(nom.equals("") || contact.equals(""))
                    Toast.makeText(context, "Veuillez renseigner votre nom complet et votre adresse électronique (ou votre numérode téléphone)", Toast.LENGTH_SHORT).show();
                else{
                    PaimentDialog dialog = new PaimentDialog(context,annonce,firebaseUser, position);
                    dialog.setContentView(R.layout.view_confirmation_reservation);
                    dialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class PaiementAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageButton methodePaiement;

        public PaiementAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            methodePaiement = itemView.findViewById(R.id.methode_paiement);
        }
    }

    private class PaimentDialog extends BottomSheetDialog {
        private final AppCompatActivity context;
        private final Annonce annonce;
        private final FirebaseUser firebaseUser;
        private final int position;

        public PaimentDialog(@NonNull @NotNull Context context, Annonce annonce, FirebaseUser firebaseUser, int position) {
            super(context);
            this.context = (AppCompatActivity) context;
            this.annonce = annonce;
            this.firebaseUser = firebaseUser;
            this.position = position;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            AppCompatTextView tvMontant = findViewById(R.id.montant);
            AppCompatTextView tvLabelPaiement = findViewById(R.id.label_paiement);
            AppCompatTextView tvNumero = findViewById(R.id.numero);
            AppCompatButton buttonConfirmer = findViewById(R.id.confirmer);
            CollectionReference reservations = FirebaseFirestore.getInstance().collection("reservationd");
            String[] numeros = new String[]{"0991234567","0825467916","0894892634","paiement@salemhouse.com","0123 4567 8910 1112","9874 5612 3012 3456"};
            String montant = annonce.getPrix()*1.5 + " $";
            String labelNumero;
            if(position <= 4)
                labelNumero = resources.getString(R.string.label_paiement_numero);
            else
                labelNumero = resources.getString(R.string.label_paiement_adresse);
            if (tvMontant != null && tvLabelPaiement != null && tvNumero != null) {
                tvMontant.setText(montant);
                tvLabelPaiement.setText(labelNumero);
                tvNumero.setText(numeros[position]);
            }
            if (buttonConfirmer != null) {
                buttonConfirmer.setOnClickListener(v -> {
                    Reservation reservation = new Reservation(firebaseUser.getUid(),annonce.getId());
                    reservations.add(reservation).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent i = new Intent();
                            context.setResult(Activity.RESULT_OK,i);
                            context.finish();
                        }
                    });
                });
            }
        }
    }
}
