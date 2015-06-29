package com.jcordeiro.shakeback.example;

import android.app.Activity;
import android.os.Bundle;

import com.jcordeiro.library.ShakeBack;
import com.jcordeiro.library.ShakeBackActivity;
import com.jcordeiro.shakeback.R;

/**
 * Written by Jon Cordeiro (http://www.github.com/jcordeiro) on 22/06/15.
 */
public class MainActivity extends ShakeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShakeBack.initialize(this, "github@joncordeiro.com", "Feedback");
    }
}
