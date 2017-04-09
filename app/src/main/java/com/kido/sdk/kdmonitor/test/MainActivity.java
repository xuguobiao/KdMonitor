package com.kido.sdk.kdmonitor.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kido.sdk.kdmonitor.core.KdMonitor;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KdMonitor.initialize(getApplicationContext());
        KdMonitor.onEvent("test1", new HashMap<String, String>());
    }
}
