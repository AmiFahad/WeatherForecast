package com.example.assignemt2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyWorker extends androidx.work.Worker {
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            String link = getInputData().getString("URL");
            URL url = new URL(link);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoInput(true);

            if(http.getResponseCode() == 200 ){

                StringBuilder builder = new StringBuilder();
                int c;
                while((c = http.getInputStream().read()) != -1){
                    builder.append((char)c);
                }
                Data json = new Data.Builder().putString("OutJson",builder.toString()).build();
                http.disconnect();
                return Result.success(json);
            }
            else {
                throw  new IOException("IT me");
            }
        }catch (Exception Ex){
            Log.e("URL ERROR",Ex.getMessage());
        }

        return Result.failure();
    }
}
