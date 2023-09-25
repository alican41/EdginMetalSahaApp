package com.alicandogru.edginmetalsahaapp;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alicandogru.edginmetalsahaapp.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class UrunAdapter extends RecyclerView.Adapter<UrunAdapter.UrunHolder> {

    private ArrayList<Bolum> bolumArrayList;



    public UrunAdapter(ArrayList<Bolum> bolumArrayList) {
        this.bolumArrayList = bolumArrayList;

    }



    @NonNull
    @Override
    public UrunHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UrunHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UrunHolder holder, int position) {
        holder.recyclerRowBinding.recyclerviewName.setText(bolumArrayList.get(position).bolum);
        holder.recyclerRowBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                if(clickedPosition != RecyclerView.NO_POSITION){
                    String bolumName = bolumArrayList.get(clickedPosition).bolum;
                    //Toast.makeText(holder.recyclerRowBinding.getRoot().getContext(),bolumName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(holder.recyclerRowBinding.getRoot().getContext(), DetayActivity.class);
                    intent.putExtra("bolumName", bolumName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.recyclerRowBinding.getRoot().getContext().startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bolumArrayList.size();
    }

    class UrunHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;

        public UrunHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;


        }
    }
}
