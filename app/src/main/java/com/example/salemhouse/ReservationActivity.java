package com.example.salemhouse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AppCompatImageView image;
    private AppCompatImageButton selectionnerPhoto;
    private AppCompatEditText nomComplet;
    private AppCompatEditText emailTelephone;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        View reservation = findViewById(R.id.reservation);
        image = findViewById(R.id.image);
        selectionnerPhoto = findViewById(R.id.selectionner_photo);
        nomComplet = findViewById(R.id.nom_complet);
        emailTelephone = findViewById(R.id.email_telephone);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        RecyclerView rvModesPaiement = findViewById(R.id.rv_modes_paiement);
        int[] images = {R.drawable.airtel,
                R.drawable.vodacom,
                R.drawable.orange2,
                R.drawable.pal,
                R.drawable.visa,
                R.drawable.ic_baseline_account_balance_72};
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                if(data != null){
                    IdpResponse response = IdpResponse.fromResultIntent(data);
                    if(response != null){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if(firebaseUser != null){
                            Log.e("USER",firebaseUser.getUid());
                            initViews(firebaseUser);
                        }
                    }
                }
            }
        });

        ActivityResultLauncher<Intent> pickPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                if (data != null) {
                    imageUri = data.getData();
                    if(imageUri != null){
                        image.setImageURI(imageUri);
                        selectionnerPhoto.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        String sAnnonce = intent.getStringExtra("annonce");
        if(sAnnonce != null){
            Annonce annonce = new Gson().fromJson(sAnnonce,Annonce.class);
            if(auth.getCurrentUser() == null){
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
                );
                startActivity.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                );
            }
            else{
                FirebaseUser firebaseUser = auth.getCurrentUser();
                initViews(firebaseUser);
            }
            progressBar.setVisibility(View.GONE);
            reservation.setVisibility(View.VISIBLE);
            FirebaseUser firebaseUser = auth.getCurrentUser();
            initViews(firebaseUser);
            rvModesPaiement.setLayoutManager(new GridLayoutManager(this, 3,RecyclerView.VERTICAL,false));
            rvModesPaiement.setAdapter(new PaiementAdapter(this,annonce,firebaseUser,images,nomComplet,emailTelephone));
        }
        selectionnerPhoto.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            pickPhoto.launch(Intent.createChooser(i,"Sélectionner votre photo de profile"));
        });
    }

    public void initViews(FirebaseUser firebaseUser){
        if(firebaseUser.getPhotoUrl() != null){
            selectionnerPhoto.setVisibility(View.GONE);
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(image);
            image.setVisibility(View.VISIBLE);
        }
        if(firebaseUser.getDisplayName() != null) {
            Log.e("DISPLAY_NAME", firebaseUser.getDisplayName());
            nomComplet.setText(firebaseUser.getDisplayName());
        }
        if(firebaseUser.getEmail() != null){
            Log.e("EMAIL", firebaseUser.getEmail());
            emailTelephone.setText(firebaseUser.getEmail());
        }
        else if(firebaseUser.getPhoneNumber() != null){
            Log.e("PHONE", firebaseUser.getPhoneNumber());
            emailTelephone.setText(firebaseUser.getPhoneNumber());
        }
    }
}