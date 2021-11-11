package com.example.salemhouse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class AnnonceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_ID_LOCATION_PERMISSION = 99;
    private Annonce annonce;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageCarousel carousel = findViewById(R.id.carousel);
        TextView prixTV = findViewById(R.id.prix);
        TextView piecesTV = findViewById(R.id.pieces);
        TextView chambresTV = findViewById(R.id.chambres);
        TextView surfaceTV = findViewById(R.id.surface);
        TextView adresseTV = findViewById(R.id.adresse);
        TextView garantieTV = findViewById(R.id.garantie);
        AppCompatButton buttonReserverMaintenant = findViewById(R.id.reserver_maintenant);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        Intent intent = getIntent();
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);
        toolbar.setNavigationOnClickListener(v -> finish());

        String id = intent.getStringExtra("id");
        ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode() == RESULT_OK)
                buttonReserverMaintenant.setActivated(false);
        });
        if(id != null){
            CollectionReference colAnnonces = FirebaseFirestore.getInstance().collection("annonces");
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AnnonceActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ID_LOCATION_PERMISSION);
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(position -> {
                if (position != null) {
                    runOnUiThread(() -> {
                        if (mapFragment != null)
                            mapFragment.getMapAsync(this);
                    });
                }
            });
            colAnnonces.document(id).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null){
                    annonce = task.getResult().toObject(Annonce.class);
                    if(annonce != null){
                        annonce.setId(id);
                        if (mapFragment != null)
                            mapFragment.getMapAsync(this);
                        List<CarouselItem> items = new ArrayList<>();
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
                        storage.child(annonce.getId()).listAll().addOnCompleteListener(list -> {
                            if(list.isSuccessful() && list.getResult() != null){
                                for(StorageReference reference : list.getResult().getItems()){
                                    reference.getDownloadUrl().addOnCompleteListener(uri -> {
                                        if(uri.isSuccessful() && uri.getResult() != null) {
                                            Log.e("URI",uri.getResult().toString());
                                            items.add(new CarouselItem(uri.getResult().toString()));
                                            carousel.setData(items);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }

        buttonReserverMaintenant.setOnClickListener(v -> {
            Intent i = new Intent(this,ReservationActivity.class);
            i.putExtra("annonce",new Gson().toJson(annonce));
            startActivity.launch(i);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_LOCATION_PERMISSION) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(position -> {
                    if (position != null) {
                        runOnUiThread(() -> {
                            if (mapFragment != null)
                                mapFragment.getMapAsync(this);
                        });
                    }
                });
            }
            else
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMinZoomPreference(14);
        googleMap.setMaxZoomPreference(18);
        LatLng localisation = (annonce.getLocalisation() != null) ?
                new LatLng(annonce.getLocalisation().getLatitude(),annonce.getLocalisation().getLongitude()) :
                new LatLng(-11.6511676,27.3855783);
        googleMap.addMarker(new MarkerOptions().position(localisation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(localisation));
    }
}