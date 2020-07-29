package com.erastus.orientate.student.signup;

import android.os.Bundle;

import com.erastus.orientate.BuildConfig;
import com.erastus.orientate.databinding.ActivityStudentSignUpBinding;
import com.erastus.orientate.student.signup.dob.DobFragment;
import com.erastus.orientate.student.signup.name.NameEmailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class StudentSignUpActivity extends AppCompatActivity implements ParentSignUpActivity {
    private ViewPager2 mViewPager;

    private StudentSignUpViewModel mViewModel;

    private SignUpSectionsAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityStudentSignUpBinding signUpBinding = ActivityStudentSignUpBinding.inflate(getLayoutInflater());

        setContentView(signUpBinding.getRoot());

        mSectionsPagerAdapter = new SignUpSectionsAdapter(getSupportFragmentManager(), getLifecycle());

        mViewPager = signUpBinding.viewPager;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setPageTransformer(new ZoomOutPageTransformer());

        WormDotsIndicator wormDotsIndicator = signUpBinding.wormDotSignUp;
        wormDotsIndicator.setViewPager2(mViewPager);

        mViewModel = new ViewModelProvider(this).get(StudentSignUpViewModel.class);

    }

    private void showLongSnackBarMessage(View view, CharSequence charSequence) {
        Snackbar.make(view, charSequence, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
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

    @Override
    public void setTab(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public StudentSignUpViewModel getViewModel() {
        return mViewModel;
    }


    private static class SignUpSectionsAdapter extends FragmentStateAdapter {
        public static final int NUM_PAGES = 2;

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
                case 0:
                    return NameEmailFragment.newInstance();
                default:
                    return DobFragment.newInstance();
            }
        }
        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }


    @RequiresApi(21)
    public class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setTranslationZ(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);
                // Move it behind the left page
                view.setTranslationZ(-1f);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    public static class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
}