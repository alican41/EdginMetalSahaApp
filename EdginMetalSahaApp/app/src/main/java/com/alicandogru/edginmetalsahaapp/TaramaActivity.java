package com.alicandogru.edginmetalsahaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alicandogru.edginmetalsahaapp.databinding.ActivityTaramaBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;

public class TaramaActivity extends AppCompatActivity {

    Button scan_btn,urunler_btn;
    TextView textView;
    private FirebaseFirestore firestore;
    private FirebaseAuth Auth;
    HashMap<String, Object> userData = new HashMap<>();
    String clickedItemName,clickedItem,email;
    ActivityTaramaBinding binding;
    ArrayList<Urun> urunArrayList;
    Timestamp tarih;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaramaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);




        urunArrayList = FeedActivity.urunArrayList;

        clickedItemName = getIntent().getStringExtra("itemName");

        scan_btn = findViewById(R.id.btn_scan);
        urunler_btn = findViewById(R.id.urunleriGor);
        textView = findViewById(R.id.text1);
        firestore = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();

        urunler_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaramaActivity.this,FeedActivity.class);
                intent.putExtra("tiktarama",2);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(TaramaActivity.this);
                intentIntegrator.setCaptureActivity(CaptureAct.class);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setPrompt("Barkodu Taratınız!");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.initiateScan();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            String contents = intentResult.getContents();
            if(contents != null){

                textView.setText(intentResult.getContents() + "----"+ clickedItemName);
                clickedItem = intentResult.getContents();

                FirebaseUser user = Auth.getCurrentUser();
                email = user.getEmail();

                // Şu anki tarihi al
                Calendar calendar = Calendar.getInstance();
                tarih = new Timestamp(calendar.getTime());

                userData = new HashMap<>();
                userData.put("Name",clickedItemName);
                userData.put("Email",email);
                userData.put("Dispo",clickedItem);
                userData.put("Tarih",tarih);
                firestore.collection(email)
                        .add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(TaramaActivity.this,"Kayıt Başarılı ", Toast.LENGTH_LONG).show();
                                Urun urun = new Urun(clickedItemName,clickedItem,tarih);

                                urunArrayList.add(urun);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TaramaActivity.this,e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }



    }

}