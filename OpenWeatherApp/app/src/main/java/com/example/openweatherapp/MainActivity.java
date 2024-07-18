package com.example.openweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button buttonCoordinates;
    TextView textView;
    EditText userZipCode;
    String zipCode;
    double lat;
    double lon;
    ImageView image1, image2, image3, image4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCoordinates = findViewById(R.id.button1);
        buttonCoordinates.setEnabled(false);
        textView = findViewById(R.id.textViewCoordinates);
        userZipCode = findViewById(R.id.editTextTextPersonName);
        image1 = findViewById(R.id.imageViewONE);
        image2 = findViewById(R.id.imageViewTWO);
        image3 = findViewById(R.id.imageViewTHREE);
        image4 = findViewById(R.id.imageViewFOUR);

        /*Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DroidSansFallback.ttf");
        textView.setTypeface(tf);*/

        userZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                zipCode = String.valueOf(charSequence);
                buttonCoordinates.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*String regex = "^\\\\d{5}(-\\\\d{4})?$";
                if(zipCode.matches(regex) == false)
                {
                    userZipCode.setText("error, please try again");
                }
                else
                {
                    buttonCoordinates.setEnabled(true);
                }*/
            }
        });

        buttonCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetWeather getWeather = new GetWeather();
                getWeather.execute("https://api.openweathermap.org/geo/1.0/zip?zip="+zipCode+",US&appid=27a3494f5125aac2d0ec397eec0e7e87");
            }
        });

    }

    public String calculateTime(JSONObject result, int i){
        int time = 0;
        try {
            time = (int) result.getJSONObject("weather").getJSONArray("hourly").getJSONObject(i).get("dt");
            Log.d("time in method", String.valueOf(time));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Date dt = new java.util.Date((long) time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy hh:mm");
        String myDate = format.format(dt);
        return myDate;
    }

    public Double getWeather (JSONObject result, int i)
    {
        double findWeather = 0;
        try {
            findWeather = (double) result.getJSONObject("weather").getJSONArray("hourly").getJSONObject(i).get("temp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return findWeather;
    }

    public String getShortDescription(JSONObject result, int i)
    {
        JSONObject findDescription = null;
        JSONObject findDescription2 = null;
        String findDescription3 = null;
        try {
            findDescription = result.getJSONObject("weather").getJSONArray("hourly").getJSONObject(i);
            findDescription2 = (JSONObject) findDescription.getJSONArray("weather").get(0);
            findDescription3 = findDescription2.getString("description").toString();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return findDescription3;

    }

    public void getImageWeather(ImageView image, JSONObject result, int i)
    {
        String desc = getShortDescription(result, i);
        if(desc.contains("cloud"))
        {
            image.setImageResource(R.drawable.cloudnew);
        }
        else if (desc.contains("rain"))
        {
            image.setImageResource(R.drawable.rainnew);
        }
        else if(desc.contains("snow"))
        {
            image.setImageResource(R.drawable.snownew);
        }
        else if(desc.contains("thunderstorm"))
        {
            image.setImageResource(R.drawable.thunderstormnew);
        }
        else if(desc.contains("clear"))
        {
            image.setImageResource(R.drawable.clearnew);
        }
        else
        {
            image.setImageResource(R.drawable.weathertemp);
        }

    }

    private class GetWeather extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject info = null;

            //LON AND LAT
            try {
                URL web = new URL(strings[0]);
                URLConnection connection = web.openConnection();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String readerInfo = reader.readLine();
                info = new JSONObject(readerInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //HOURLY WEATHER
            JSONObject weatherObject = null;
            try {
                lon = (double) info.get("lon");
                lat = (double) info.get("lat");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL weather = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=current,minutely,daily,alerts&units=imperial&appid=27a3494f5125aac2d0ec397eec0e7e87");
                Log.d("website", String.valueOf(weather));
                URLConnection connection = weather.openConnection();
                InputStream in1 = connection.getInputStream();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
                String readerInfo1 = reader1.readLine();
                weatherObject = new JSONObject(readerInfo1);
                info.put("weather", weatherObject);
                Log.d("weather", weatherObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return info;
        }

        protected void onPostExecute(JSONObject result) {
            TextView txt = findViewById(R.id.textViewCoordinates);
            try {
                //COORDINATES
                txt.setText("lon: "+result.get("lon").toString()+"\nlat: "+result.get("lat").toString()+"\n"+result.get("name"));

                //WEATHER INFORMATION
                String time;
                TextView text1 = null;
                TextView text2 = null;
                TextView text3 = null;
                TextView text4 = null;

                //FOR THE TIME
                for(int i=0; i<4; i++)
                {
                    time = calculateTime(result, i);
                    switch(i)
                    {
                        case 0: { text1 = findViewById(R.id.textViewHourOne); text1.setText("time: "+time); }
                        case 1: { text2 = findViewById(R.id.textViewHourTwo); text2.setText("time: "+time); }
                        case 2: { text3 = findViewById(R.id.textViewHourThree); text3.setText("time: "+time); }
                        case 3: { text4 = findViewById(R.id.textViewHourFour); text4.setText("time: "+time); }
                    }
                }

                //WEATHER
                text1.append("\ntemp: "+getWeather(result, 0)+" \u00B0F");
                text2.append("\ntemp: "+getWeather(result, 1)+" \u00B0F");
                text3.append("\ntemp: "+getWeather(result, 2)+" \u00B0F");
                text4.append("\ntemp: "+getWeather(result, 3)+" \u00B0F");

                //SHORT DESCRIPTION
                text1.append(" \n"+getShortDescription(result, 0));
                text2.append(" \n"+getShortDescription(result, 1));
                text3.append(" \n"+getShortDescription(result, 2));
                text4.append(" \n"+getShortDescription(result, 3));

                //IMAGES
                getImageWeather(image1, result, 0);
                getImageWeather(image2, result, 1);
                getImageWeather(image3, result, 2);
                getImageWeather(image4, result, 3);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}


