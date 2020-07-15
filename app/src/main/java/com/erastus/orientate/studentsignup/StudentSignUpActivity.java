package com.erastus.orientate.studentsignup;

import android.os.Bundle;

import com.erastus.orientate.BuildConfig;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentSignUpBinding;
import com.erastus.orientate.studentsignup.dob.DobFragment;
import com.erastus.orientate.studentsignup.name.NameEmailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class StudentSignUpActivity extends AppCompatActivity {
    public static final int NUM_PAGES = 2;

    private ViewPager2 mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityStudentSignUpBinding signUpBinding = ActivityStudentSignUpBinding.inflate(getLayoutInflater());

        setContentView(signUpBinding.getRoot());

        SignUpSectionsAdapter sectionsPagerAdapter = new SignUpSectionsAdapter(getSupportFragmentManager(), getLifecycle());

        mViewPager = signUpBinding.viewPager;
        mViewPager.setAdapter(sectionsPagerAdapter);

        FloatingActionButton fab = signUpBinding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        WormDotsIndicator wormDotsIndicator = signUpBinding.wormDotSignUp;
        wormDotsIndicator.setViewPager2(mViewPager);

    }

    private void showLongSnackBarMessage(View view, CharSequence charSequence) {
        Snackbar.make(view, charSequence, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }


    private static class SignUpSectionsAdapter extends FragmentStateAdapter {
        public SignUpSectionsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (BuildConfig.DEBUG && !(position == 0 || position == 1)) {
                throw new AssertionError("Assertion failed");
            }

        switch (position) {
            case 1:
                return DobFragment.newInstance();
            default:
                return NameEmailFragment.newInstance();
        }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}