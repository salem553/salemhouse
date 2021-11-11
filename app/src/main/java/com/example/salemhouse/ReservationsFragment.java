package com.example.salemhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salemhouse.databinding.FragmentReservationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {
    private static final String ANNONCES = "annonces";
    private static final String RESERVATIONS = "reservations";
    private FirebaseAuth auth;
    private FragmentReservationsBinding binding;
    private CollectionReference colAnnonces;
    private CollectionReference colReservations;
    private StorageReference refAnnonces;
    private ProgressBar progressBar;
    private AppCompatTextView messageAucuneReservation;
    private RecyclerView rvReservations;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        colAnnonces = firestore.collection(ANNONCES);
        colReservations = firestore.collection(RESERVATIONS);
        refAnnonces = FirebaseStorage.getInstance().getReference();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservationsBinding.inflate(inflater, container, false);
        progressBar = binding.progressBar;
        messageAucuneReservation = binding.messageAucuneReservation;
        rvReservations = binding.rvReservations;
        if(auth.getCurrentUser() != null){
            colReservations.whereEqualTo("uid",auth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null){
                    progressBar.setVisibility(View.GONE);
                    if(task.getResult().size() != 0){
                        List<Reservation> reservations = new ArrayList<>();
                        for(DocumentSnapshot snapshot : task.getResult()){
                            Reservation reservation = snapshot.toObject(Reservation.class);
                            if (reservation != null) {
                                reservation.setId(snapshot.getId());
                                colAnnonces.document(reservation.getAid()).get().addOnCompleteListener(aTask -> {
                                    if(aTask.isSuccessful() && aTask.getResult() != null){
                                        Annonce annonce = aTask.getResult().toObject(Annonce.class);
                                        if (annonce != null) {
                                            annonce.setId(aTask.getResult().getId());
                                            reservation.setAnnonce(annonce);
                                            reservations.add(reservation);
                                        }
                                    }
                                });
                            }
                        }
                        rvReservations.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false));
                        rvReservations.setAdapter(new ReservationAdapter(requireActivity(), colReservations, reservations, refAnnonces));
                    }
                    else
                        messageAucuneReservation.setVisibility(View.VISIBLE);
                }
            });
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}