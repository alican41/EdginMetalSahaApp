package com.alicandogru.edginmetalsahaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;


import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alicandogru.edginmetalsahaapp.databinding.ActivityFeedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import java.util.ArrayList;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;


import java.util.Map;


public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityFeedBinding binding;

    public static ArrayList<Urun> urunArrayList;

    UrunAdapter urunAdapter;
    ArrayList<Bolum> bolumArrayList;
    HashSet<String> uniqueBolumSet;
    int tikk=0;
    Intent intent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        ActivityCompat.requestPermissions(FeedActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        uniqueBolumSet = new HashSet<>();
        urunArrayList = new ArrayList<>();
        bolumArrayList = new ArrayList<>();


        intent = getIntent();
        if (intent != null) {
            tikk = intent.getIntExtra("tikdetay",0);
            tikk = intent.getIntExtra("tiktarama",0);
        }

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();



        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        urunAdapter = new UrunAdapter(bolumArrayList);



        binding.recyclerview.setAdapter(urunAdapter);


    }

    private void getData(){

        FirebaseUser user= auth.getCurrentUser();
        firebaseFirestore.collection(user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String userEmail = (String) data.get("Email");
                        String urunBolum = (String) data.get("Name");
                        String dispoNo = (String) data.get("Dispo");
                        Timestamp timestamp = (Timestamp) data.get("Tarih");

                        Urun urun = new Urun(urunBolum, dispoNo, timestamp);

                        if (tikk == 1) {
                            urunArrayList.clear();
                        }
                        urunArrayList.add(urun);


                        if (!uniqueBolumSet.contains(urunBolum)) {
                            uniqueBolumSet.add(urunBolum);
                            Bolum bolum = new Bolum(urunBolum);
                            bolumArrayList.add(bolum);
                        }
                    }
                    urunAdapter.notifyDataSetChanged();


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.tarama) {
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.signout) {
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        } else if (item.getItemId() == R.id.silme) {

            FirebaseUser user = auth.getCurrentUser();
            firebaseFirestore.collection(user.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // Koleksiyondaki belgeler başarıyla alındı
                                for (DocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                                urunArrayList.clear();
                                // Adaptörü temizleyin
                                binding.recyclerview.setAdapter(null);

                                // Layout yöneticisini temizleyin
                                binding.recyclerview.setLayoutManager(null);
                                urunAdapter.notifyDataSetChanged();
                                Toast.makeText(FeedActivity.this, "Silme işlemi gerçekleşti", Toast.LENGTH_LONG).show();
                            } else {
                                // Koleksiyondaki belgeleri alırken hata oluştu
                                Toast.makeText(FeedActivity.this, "Silme işlemi başarısız oldu!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        } else if (item.getItemId() == R.id.excel) {

            // Urunleri alfabetik olarak siralama
            Collections.sort(urunArrayList, new Comparator<Urun>() {
                @Override
                public int compare(Urun urun1, Urun urun2) {
                    return urun1.name.compareTo(urun2.name);
                }
            });
            // Yeni bir Excel çalışma kitabı (workbook) oluştur
            Workbook workbook = new XSSFWorkbook();

            // Yeni bir Excel çalışma sayfası (sheet) oluştur
            Sheet sheet = workbook.createSheet("Tablo1");

            // Başlık için bir satır oluştur
            Row headerRow = sheet.createRow(0);

            // Başlık hücreleri için stiller oluştur
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Başlık hücrelerini oluştur ve stilleri uygula
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Bölüm");
            headerCell1.setCellStyle(headerCellStyle);

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Dispo");
            headerCell2.setCellStyle(headerCellStyle);

            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue("Tarih");
            headerCell3.setCellStyle(headerCellStyle);

            // Veri için bir satır oluştur
            Row dataRow = sheet.createRow(1);

            int rowNum = 1; // İlk satır başlık olduğu için 1'den başlıyoruz

            for (Urun urun : urunArrayList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(urun.name);
                row.createCell(1).setCellValue(urun.barkod);
                row.createCell(2).setCellValue(urun.timestamp.toDate().toString());
            }



            try {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "dosya2.xlsx");
                FileOutputStream outputStream = new FileOutputStream(file);
                workbook.write(outputStream);
                outputStream.close();
                System.out.println("Dosya oluşturuldu.");

                Uri fileUri = FileProvider.getUriForFile(FeedActivity.this, "com.alicandogru.edginmetalsahaapp.fileprovider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Dosyanın indirilmesini sağlamak için kullanıcıya seçenek sunar
                Intent downloadIntent = Intent.createChooser(intent, "Dosyayı indirin");
                downloadIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(downloadIntent);
            } catch (IOException e) {
                e.printStackTrace();

                // Workbook ve kaynakları serbest bırak
                try {
                    workbook.close();
                } catch (IOException en) {
                    en.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }
}