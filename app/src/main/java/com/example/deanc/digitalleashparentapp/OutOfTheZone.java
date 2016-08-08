package com.example.deanc.digitalleashparentapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by DeanC on 5/4/2016.
 */
public class OutOfTheZone extends Activity {

    TextView Time = null;
    TextView Lat = null;
    TextView Lon = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_out_of_zone);

        Time = (TextView)findViewById(R.id.child_time);
        Time.setText("Last GPS Update:\n" + MainActivity.child_time);

        setFonts();

    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView)findViewById(R.id.fail_notification);
        tv1.setTypeface(tf);
        TextView tv2 = (TextView)findViewById(R.id.child_time);
        tv2.setTypeface(tf);
        Button bt1 = (Button)findViewById(R.id.uhoh);
        bt1.setTypeface(tf);
        Button bt2 = (Button)findViewById(R.id.map);
        bt2.setTypeface(tf);

    }

    public void returnToMain(View view) {

        Intent intent = new Intent(OutOfTheZone.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void mapIT(View view){

        Uri uri = Uri.parse(MainActivity.URL); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }


}
