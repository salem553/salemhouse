package com.example.salemhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salemhouse.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String ANNONCES = "annonces";
    private CollectionReference colAnnonces;
    private StorageReference refAnnonces;
    private ProgressBar progressBar;
    private RecyclerView rvAnnonces;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colAnnonces = FirebaseFirestore.getInstance().collection(ANNONCES);
        refAnnonces = FirebaseStorage.getInstance().getReference();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        progressBar = binding.progressBar;
        rvAnnonces = binding.rvAnnonces;

        colAnnonces.get().addOnCompleteListener(task -> {
           if(task.isSuccessful() && task.getResult() != null){
               progressBar.setVisibility(View.GONE);
               List<Annonce> annonces = new ArrayList<>();
               for(DocumentSnapshot snapshot : task.getResult()){
                   Annonce annonce = snapshot.toObject(Annonce.class);
                   if (annonce != null) {
                       annonce.setId(snapshot.getId());
                       annonces.add(annonce);
                   }
               }
               if(annonces.size() != 0){
                   rvAnnonces.setLayoutManager(new LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false));
                   rvAnnonces.setAdapter(new AnnonceAdapter(requireActivity(),annonces,refAnnonces));
               }
           }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}