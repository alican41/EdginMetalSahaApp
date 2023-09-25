package com.alicandogru.edginmetalsahaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("Gelen Siparişler",R.drawable.a));
        items.add(new Item("Depoya Verilen Siparişler",R.drawable.b));
        items.add(new Item("Dış Raf 1",R.drawable.c));
        items.add(new Item("Dış Raf 2",R.drawable.c));
        items.add(new Item("Dış Raf 3",R.drawable.c));
        items.add(new Item("Dış Raf 4",R.drawable.c));
        items.add(new Item("Dış Raf 5",R.drawable.c));
        items.add(new Item("Dış Raf 6",R.drawable.c));
        items.add(new Item("Dış Raf 7",R.drawable.c));
        items.add(new Item("Dış Raf 8",R.drawable.c));
        items.add(new Item("Dış Raf 9",R.drawable.c));
        items.add(new Item("Dış Raf 10",R.drawable.c));
        items.add(new Item("El İşleme",R.drawable.d));
        items.add(new Item("Zımpara",R.drawable.e));
        items.add(new Item("Kaynak",R.drawable.f));
        items.add(new Item("Kumlama",R.drawable.g));
        items.add(new Item("Seramik",R.drawable.h));
        items.add(new Item("Harf El İşleme",R.drawable.i));
        items.add(new Item("Bitmiş Cnc",R.drawable.j));
        items.add(new Item("Cnc",R.drawable.k));
        items.add(new Item("Delme",R.drawable.l));
        items.add(new Item("Laterne",R.drawable.m));
        items.add(new Item("Paslanmaz",R.drawable.n));
        items.add(new Item("Patina",R.drawable.o));
        items.add(new Item("Kontrol",R.drawable.p));
        items.add(new Item("Cam",R.drawable.r));
        items.add(new Item("Paket",R.drawable.s));
        items.add(new Item("Yapılamayan Siparişler",R.drawable.t));
        items.add(new Item("Harfler",R.drawable.u));
        items.add(new Item("Yapılamayan Harfler",R.drawable.v));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));
    }
}