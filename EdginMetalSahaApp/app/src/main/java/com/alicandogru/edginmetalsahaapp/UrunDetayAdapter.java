package com.alicandogru.edginmetalsahaapp;


import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alicandogru.edginmetalsahaapp.databinding.RecyclerDetayRowBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class UrunDetayAdapter extends RecyclerView.Adapter<UrunDetayAdapter.UrunDetayHolder> {

    private ArrayList<Urun> urunArrayList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    public UrunDetayAdapter(ArrayList<Urun> urunArrayList) {
        this.urunArrayList = urunArrayList;

    }
    public void setFilteredList(ArrayList<Urun> filteredList){
        this.urunArrayList = filteredList;

    }
    @NonNull
    @Override
    public UrunDetayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerDetayRowBinding recyclerDetayRowBinding = RecyclerDetayRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UrunDetayHolder(recyclerDetayRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UrunDetayHolder holder, int position) {
        holder.recyclerDetayRowBinding.recyclerviewBolum.setText(urunArrayList.get(position).name);
        holder.recyclerDetayRowBinding.recyclerviewDispo.setText(urunArrayList.get(position).barkod);
    }

    @Override
    public int getItemCount() {
        return urunArrayList.size();
    }

    public class UrunDetayHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RecyclerDetayRowBinding recyclerDetayRowBinding;
        public UrunDetayHolder(RecyclerDetayRowBinding recyclerDetayRowBinding) {
            super(recyclerDetayRowBinding.getRoot());
            this.recyclerDetayRowBinding = recyclerDetayRowBinding;

            recyclerDetayRowBinding.deleteid.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                CollectionReference collectionRef = db.collection(user.getEmail());
                // Silinecek belgeleri almak için bir sorgu oluşturun
                Query query = collectionRef.whereEqualTo("Name",urunArrayList.get(position).name)
                        .whereEqualTo("Dispo",urunArrayList.get(position).barkod);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Her bir belgeyi silin
                                document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Başarıyla silindi
                                        Toast.makeText(recyclerDetayRowBinding.getRoot().getContext(), "Silme işlemi başarılı", Toast.LENGTH_LONG).show();
                                        // Tıklanan resmin olduğu kısımı listeden silme işlemi
                                        urunArrayList.remove(position); // Öğeyi listeden kaldır
                                        notifyItemRemoved(position); // RecyclerView'a güncellemeyi bildir
                                        Intent intent = new Intent(recyclerDetayRowBinding.getRoot().getContext(),FeedActivity.class);
                                        intent.putExtra("tikdetay",1);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        recyclerDetayRowBinding.getRoot().getContext().startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Silme hatası oluştu
                                        Toast.makeText(recyclerDetayRowBinding.getRoot().getContext(), "Silme işlemi gerçekleşmedi!!", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        } else {
                            // Sorgu başarısız oldu
                            Toast.makeText(recyclerDetayRowBinding.getRoot().getContext(), "Sorgu Başarısız!!", Toast.LENGTH_LONG).show();

                        }
                    }
                });


            }
        }
    }
}
