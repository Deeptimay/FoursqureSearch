package com.deeptimay.yuluvenues.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.deeptimay.yuluvenues.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // The app-specific constant when requesting the location permission
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    // The ImageView of the logo
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaches a listener for animation/transition to the logo
        logo = findViewById(R.id.flag_bttn);
        logo.setOnClickListener(this);

        // Requests for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Adds a jitter animation to the logo
//        RotateAnimation rotateAnimation = new RotateAnimation(0, 2, 50, 50);
//        rotateAnimation.setDuration(10);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);
//        rotateAnimation.setRepeatMode(Animation.REVERSE);
//        logo.startAnimation(rotateAnimation);
    }

    @Override
    public void onClick(View view) {

        // Defines a new alpha/scale animation
        Animation click = AnimationUtils.loadAnimation(this, R.anim.click);

        // Defines a listener to transition to the PlacePickerActivity after the animation completes
        click.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getApplicationContext(), PlacePickerActivity.class);
                    startActivity(i);
                } else {
                    // Notifies the user if there are insufficient location permissions
                    Toast.makeText(getApplicationContext(), "Mr. Jitters is missing permissions to access your location!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Attaches the alpha/scale animation to the view
        view.startAnimation(click);
    }
}
