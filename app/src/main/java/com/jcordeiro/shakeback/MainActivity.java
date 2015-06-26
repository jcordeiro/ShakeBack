package com.jcordeiro.shakeback;

import android.os.Bundle;

/**
 * Created by Jon Cordeiro (http://www.github.com/jcordeiro) on 22/06/15.
 */
public class MainActivity extends ShakeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ShakeBack.initialize(this, "contact@joncordeiro.com", "Feedback");

        ShakeBack.initialize(this, "contact@joncordeiro.com", "Feedback")
                .setVibrationEnabled(true)
                .setDialogIcon(R.drawable.ic_phone_shake)
                .setDialogTitle("Title here!")
                .setDialogMessage("MESSAGE IS HERE!")
                .setShakeSensitivity(ShakeBack.ShakeSensitivity.EASY);

    }
}
