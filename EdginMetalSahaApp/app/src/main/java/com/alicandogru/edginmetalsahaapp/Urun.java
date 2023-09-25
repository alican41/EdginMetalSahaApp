package com.alicandogru.edginmetalsahaapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class Urun {

    public String name;
    public String barkod;
    public Timestamp timestamp;

    public Urun(String name, String barkod,Timestamp timestamp) {
        this.name = name;
        this.barkod = barkod;
        this.timestamp = timestamp;

    }
}
