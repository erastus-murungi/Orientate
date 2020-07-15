package com.erastus.orientate.studentsignup;

import android.os.Bundle;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentSignUpBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.erastus.orientate.studentsignup.ui.main.SectionsPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class StudentSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityStudentSignUpBinding signUpBinding = ActivityStudentSignUpBinding.inflate(getLayoutInflater());

        setContentView(signUpBinding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        WormDotsIndicator wormDotsIndicator = signUpBinding.wormDotSignUp;
        wormDotsIndicator.setViewPager(viewPager);
    }
}