package com.alicandogru.edginmetalsahaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alicandogru.edginmetalsahaapp.databinding.ActivityDetayBinding;

import java.util.ArrayList;
import java.util.List;

public class DetayActivity extends AppCompatActivity {

    private ActivityDetayBinding detayBinding;
    ArrayList<Urun> urunArrayList;
    ArrayList<Urun> filtrelenmisArrayList;
    UrunDetayAdapter urunDetayAdapter;
    Intent intent;
    String bolumName;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detayBinding = ActivityDetayBinding.inflate(getLayoutInflater());
        View view = detayBinding.getRoot();
        setContentView(view);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        intent = getIntent();
        if (intent != null) {
            bolumName = intent.getStringExtra("bolumName");
        }


        urunArrayList =FeedActivity.urunArrayList;

        filtrelenmisArrayList = new ArrayList<>();
        for (Urun urun : urunArrayList) {
            // Filtreleme kriterlerini burada kontrol edin
            if (urun.name.equals(bolumName)) {
                filtrelenmisArrayList.add(urun);
            }
        }
        detayBinding.recyclerviewdetay.setLayoutManager(new LinearLayoutManager(this));
        urunDetayAdapter = new UrunDetayAdapter(filtrelenmisArrayList);
        detayBinding.recyclerviewdetay.setAdapter(urunDetayAdapter);
    }

    private void filterList(String text) {
        ArrayList<Urun> filteredList = new ArrayList<>();
        for (Urun urun : filtrelenmisArrayList){
            if(urun.barkod.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(urun);
                urunDetayAdapter.notifyDataSetChanged();
            }
        }
        if(!filteredList.isEmpty()){
            urunDetayAdapter.setFilteredList(filteredList);
        }else{
            Toast.makeText(this, "Aradığınız Dispo Bulunamadı!", Toast.LENGTH_LONG).show();
        }
    }
}