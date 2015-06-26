package com.jcordeiro.shakeback;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Jon Cordeiro (http://www.github.com/jcordeiro) on 22/06/15.
 */
public class ShakeBackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShakeBack.initialize(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeBack.activate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ShakeBack.deactivate();
    }
}
