package com.example.assignemt2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.InputQueue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String url = "https://api.openweathermap.org/data/2.5/weather/";
    private final String appid = "1738fb8a914f08bce7ab4147da4e8d72";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

     public  void  getweatherDetails (View V){
         String Country = ((EditText)findViewById(R.id.etCountry)).getText().toString();
         String city = ((EditText)findViewById(R.id.etCity)).getText().toString();

         if ((! Country.isEmpty()) || (! city.isEmpty())){

             String requsetUrl = url+"?q="+city;
             if(!Country.isEmpty()){
                 requsetUrl += ","+Country;
             }
             requsetUrl += "&appid="+appid;
             Log.d("URL",requsetUrl);

             Data data = new Data.Builder().putString("URL",requsetUrl).build();
             OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWorker.class).setInputData(data).build();
             WorkManager.getInstance(getApplicationContext()).enqueue(request);
             WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(request.getId())
                     .observe(this, new Observer<WorkInfo>() {
                         @Override
                         public void onChanged(WorkInfo workInfo) {
                             if(workInfo!=null){
                                 Log.i("Status",workInfo.getState().name());
                                 if(workInfo.getState().isFinished()){
                                     String s = workInfo.getOutputData().getString("OutJson");
                                     Log.i("JSON Responce", s);
                                     PrintResult(s);
                                 }
                             }
                         }
                     });


     }
}


public void PrintResult(String s){
        StringBuilder builder = new StringBuilder();
        try {
            JSONObject json = new JSONObject(s);
            JSONObject main =  json.getJSONObject("main");
            builder.append("Temperature :"+ main.getString("temp")+"\n");
            builder.append( "Temperature Feels :"+main.getString("feels_like")+"\n");
            builder.append("Temperature Min :"+ main.getString("temp_min")+"\n");
            builder.append("Temperature Max :"+ main.getString("temp_max")+"\n");
            builder.append("Pressure :"+ main.getString("pressure")+"\n");
           Toast.makeText(this,builder.toString(),Toast.LENGTH_LONG).show();
            ((TextView)findViewById(R.id.tvResult)).setText(builder.toString());

        }
        catch (Exception ex){
            Log.e("Json",ex.getMessage());
        }
}
}