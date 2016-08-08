package com.example.deanc.digitalleashparentapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "Sample JSON";

    double parent_lat, parent_lon, child_lat, child_lon, distance, radiusToMeters;

    String jsonString, UN, RAD, parent_LAT, parent_LON;

    public static String child_time, child_LAT, child_LON, URL;

    String http = null;

    EditText userName = null;
    EditText radius = null;
    EditText latitude = null;
    EditText longitude = null;

    AlertDialog.Builder alert1, alert2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFonts();

        userName = (EditText) findViewById(R.id.get_username);
        radius = (EditText) findViewById(R.id.get_radius);
        latitude = (EditText) findViewById(R.id.get_latitude);
        longitude = (EditText) findViewById(R.id.get_longitude);


        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 1000, (float) 10, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        });

        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected){
            Toast toast = new Toast(this);
            toast.makeText(MainActivity.this, "You do not have an active network connection!",Toast.LENGTH_LONG).show();
        }

    }

    public void create(View view) throws JSONException {

        UN = userName.getText().toString();
        RAD = radius.getText().toString();
        parent_LAT = latitude.getText().toString();
        parent_LON = longitude.getText().toString();

        http = "https://turntotech.firebaseio.com/digitalleash/" + UN + ".json";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("username", UN);
        jsonObject.put("radius", RAD);
        jsonObject.put("latitude", parent_LAT);
        jsonObject.put("longitude", parent_LON);

        jsonString = jsonObject.toString();

        new SendJsonDataToServer().execute(jsonString);

        showInfoEnteredAlert();

    }

    public void showInfoEnteredAlert() {

        alert1 = new AlertDialog.Builder(this)
                .setTitle("Info Saved To Server:")
                .setMessage("User Name: " + UN + "\nRadius: " + RAD + "\nLatitude: " + parent_LAT + "\nLongitude: " + parent_LON)
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        closeContextMenu();
                    }
                });
        alert1.show();
    }

    private class SendJsonDataToServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonString);
                out.close();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    System.out.println("" + sb.toString());

                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }

    private class GetJsonDataFromServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
              //  urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                //urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    in.close();

                    System.out.println("" + sb.toString());
                    return sb.toString();

                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //Log.d(TAG, "JSON Raw Data" + result);

            try {

                //Log.d(TAG, "JSON Corrected Data" + result);

                //JSONArray jarray = new JSONArray(result);

                // Get JSON data from JSON object and set the data in our text views.
                JSONObject json = new JSONObject(result);

                child_LAT = json.getString("child_latitude");
                child_LON = json.getString("child_longitude");
                child_time = json.getString("child_current_time");
                RAD = json.getString("radius");

                URL = "http://maps.google.com/?q=" + child_LAT + "," + child_LON;

                calculateDistance();

                showStatus();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class UpdateJsonDataToServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();

            http = "https://turntotech.firebaseio.com/digitalleash/" + UN + ".json";

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PATCH");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonString);
                out.close();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    System.out.println("" + sb.toString());

                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }

    public void checkStatus(View view) {

        UN = userName.getText().toString();
        parent_LAT = latitude.getText().toString();
        parent_LON = longitude.getText().toString();

        http = "https://turntotech.firebaseio.com/digitalleash/" + UN + ".json";

        new GetJsonDataFromServer().execute(http);





    }

    public void calculateDistance() {

        parent_lat = Double.parseDouble(parent_LAT);
        parent_lon = Double.parseDouble(parent_LON);
        child_lat = Double.parseDouble(child_LAT);
        child_lon = Double.parseDouble(child_LON);

        Location parent = new Location("Parent");
        parent.setLatitude(parent_lat);
        parent.setLongitude(parent_lon);

        Location child = new Location("Child");
        child.setLatitude(child_lat);
        child.setLongitude(child_lon);

        distance = parent.distanceTo(child);

        radiusToMeters = Double.parseDouble(RAD);
        radiusToMeters = radiusToMeters * 1609.34;
    }

    public void showStatus() {

        if (distance <= radiusToMeters){

            Intent intent = new Intent(MainActivity.this, InZone.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }else if (distance > radiusToMeters){

            Intent intent = new Intent(MainActivity.this, OutOfTheZone.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {

            alert2 = new AlertDialog.Builder(this)
                    .setTitle("Location Unknown")
                    .setMessage("Your Child has turned off their phone an is not broadcasting GPS updates.")
                    .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            closeContextMenu();
                        }
                    });
            alert2.show();


        }
    }

    public void update(View view) throws JSONException {

        UN = userName.getText().toString();
        RAD = radius.getText().toString();
        parent_LAT = latitude.getText().toString();
        parent_LON = longitude.getText().toString();

        http = "https://turntotech.firebaseio.com/digitalleash/" + UN + ".json";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("username", UN);
        jsonObject.put("radius", RAD);
        jsonObject.put("latitude", parent_LAT);
        jsonObject.put("longitude", parent_LON);

        jsonString = jsonObject.toString();

        new UpdateJsonDataToServer().execute(jsonString);

        showInfoEnteredAlert();

    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView) findViewById(R.id.header);
        tv1.setTypeface(tf);
        TextView tv2 = (TextView) findViewById(R.id.username);
        tv2.setTypeface(tf);
        EditText e1 = (EditText) findViewById(R.id.get_username);
        e1.setTypeface(tf);
        TextView tv3 = (TextView) findViewById(R.id.radius);
        tv3.setTypeface(tf);
        EditText e2 = (EditText) findViewById(R.id.get_radius);
        e2.setTypeface(tf);
        TextView tv4 = (TextView) findViewById(R.id.latitude);
        tv4.setTypeface(tf);
        TextView tv5 = (TextView) findViewById(R.id.longitude);
        tv5.setTypeface(tf);
        EditText e3 = (EditText) findViewById(R.id.get_latitude);
        e3.setTypeface(tf);
        EditText e4 = (EditText) findViewById(R.id.get_longitude);
        e4.setTypeface(tf);
        Button b1 = (Button) findViewById(R.id.create);
        b1.setTypeface(tf);
        Button b2 = (Button) findViewById(R.id.check_status);
        b2.setTypeface(tf);
        Button b3 = (Button) findViewById(R.id.update);
        b3.setTypeface(tf);
    }

}
