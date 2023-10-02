package com.abc.whetherapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText e1;
    Button b1;
    TextView t1;
    ImageView i1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        e1 = findViewById(R.id.editText);
        b1 = findViewById(R.id.button);
        t1 = findViewById(R.id.textView);
        i1=  findViewById(R.id.imageView);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city_name = e1.getText().toString();
                i1.setVisibility(View.INVISIBLE);
                t1.setText("");
                if(city_name.isEmpty()){
                    e1.setError("Enter a city name");
                }
                else {
                    fetchWeather(city_name);
                }
            }
        });
    }

    private void fetchWeather(String city) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://weatherapi-com.p.rapidapi.com/current.json?q=" + city)
                .get()
                .addHeader("X-RapidAPI-Key", "95d04d9944msh934471a782cd7b4p109f57jsn6d0297c64d15")
                .addHeader("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = "Error: " + e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t1.setText(errorMessage);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                String condition = jsonObject.getJSONObject("current").getJSONObject("condition").getString("text");
                                String temperature = jsonObject.getJSONObject("current").getString("temp_c");
                                temperature=temperature+" °C";
                                String location = jsonObject.getJSONObject("location").getString("name")+", "+jsonObject.getJSONObject("location").getString("region")+", "+jsonObject.getJSONObject("location").getString("country");
                                String humidity= jsonObject.getJSONObject("current").getString("humidity");
                                String wind = "Wind Speed= "+jsonObject.getJSONObject("current").getString("wind_kph")+" Kph" +
                                        "\n" + "Wind Degree= " + jsonObject.getJSONObject("current").getString("wind_degree")+"°"+
                                        "\n" + "Wind Direction= " + jsonObject.getJSONObject("current").getString("wind_dir");

                                String uv=jsonObject.getJSONObject("current").getString("uv");
                                String pressure=jsonObject.getJSONObject("current").getString("pressure_mb")+" mb";

                                String x="Condition= "+condition+"\n"+"Temperature= "+temperature+"\n"+"Location= "+location+"\n"+"Pressure= "+pressure+"\n"+"Humidity= "+humidity+"%"+"\n"+"UV Index= "+uv +"\n"+wind;
                                t1.setText(x);
                                String imageUrl = jsonObject.getJSONObject("current").getJSONObject("condition").getString("icon");
                                i1.setVisibility(View.VISIBLE);
                                Picasso.get().load("https:"+imageUrl).into(i1);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }



                        }
                    });
                } else {
                    final String errorMessage = "Error: " + response.code() + " " + response.message();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t1.setText(errorMessage);
                        }
                    });
                }
            }
        });
    }

}
