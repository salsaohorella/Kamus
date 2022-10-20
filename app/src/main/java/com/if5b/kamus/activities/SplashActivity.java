package com.if5b.kamus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import com.if5b.kamus.R;
import com.if5b.kamus.database.KamusHelper;
import com.if5b.kamus.databinding.ActivitySplashBinding;
import com.if5b.kamus.models.Kamus;
import com.if5b.kamus.utilities.AppPreference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<Void, Integer, Void> {
        String TAG = LoadData.class.getSimpleName();
        KamusHelper kamushelper;
        AppPreference appPreference;
        double progress;
        double maxprogress = 100;

        @Override
        protected void onPreExecute() {
            kamushelper = new KamusHelper(SplashActivity.this);
            appPreference = new AppPreference(SplashActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Boolean firstRun = appPreference.getFirstRun();
            if (firstRun){
                ArrayList<Kamus>kamusEnglishIndonesia = preLoadRawEnglishIndonesia();
                kamushelper.open();

                progress = 30;
                publishProgress((int) progress);
                Double progressMaxInsert = 88.0;
                Double progressDiff = (progressMaxInsert = progress) / kamusEnglishIndonesia.size();

                for (Kamus kamus : kamusEnglishIndonesia) {
                    kamushelper.insertDataEnglishIndonesia(kamus);
                    progress += progressDiff;
                    publishProgress((int) progress);
                }

                kamushelper.close();
                appPreference.setFirstRun(false);
                publishProgress((int) maxprogress);


            }else {
                try {
                    synchronized (this){
                        this.wait(1000);
                        publishProgress(50);

                        this.wait(1000);
                        publishProgress((int) maxprogress);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            binding.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onPostExecute(unused);
        }
    }

    private ArrayList<Kamus>preLoadRawEnglishIndonesia(){
        ArrayList<Kamus> kamusArrayList = new ArrayList<>();
        String Line = null;
        BufferedReader reader;
        try {
            Resources res = getResources();
            InputStream raw_dict = res.openRawResource(R.raw.english_indonesia);

            reader = new BufferedReader(new InputStreamReader(raw_dict));
            int count = 0;
            do {
                Line = reader.readLine();
                String[] splitstr = Line.split("\t");

                Kamus kamus;
                kamus = new Kamus(splitstr[0], splitstr[1]);
                kamusArrayList.add(kamus);
                count++;

            }while (Line != null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return kamusArrayList;
    }
}