package com.erastus.orientate.student.web;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityWebBinding;

public class WebActivity extends AppCompatActivity {
    private ActivityWebBinding webBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webBinding = ActivityWebBinding.inflate(getLayoutInflater());

        setContentView(webBinding.webViewCustom);


    }
}