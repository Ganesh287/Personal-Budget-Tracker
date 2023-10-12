package com.example.cashtrack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView appname;
        appname = findViewById(R.id.title_main);

        // Calculate the translation value to start from the right and stop at the center of the screen
        int centerX = getResources().getDisplayMetrics().widthPixels / 2;
        int textWidth = appname.getWidth();
        int translationX = centerX + (textWidth / 2);

        appname.setTranslationX(translationX); // Set the initial translation to the right of the screen
        appname.animate().translationX(0).setDuration(1800).setStartDelay(0); // Animate towards the center

        LottieAnimationView animationView = findViewById(R.id.animationView);

        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });
    }
}
