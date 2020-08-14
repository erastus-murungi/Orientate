package com.erastus.orientate.student.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentNavBinding;
import com.erastus.orientate.institution.models.Institution;
import com.erastus.orientate.models.Orientation;
import com.erastus.orientate.student.announcements.AnnouncementFragment;
import com.erastus.orientate.student.chat.ChatActivity;
import com.erastus.orientate.student.event.EventFragment;
import com.erastus.orientate.student.info.InfoFragment;
import com.erastus.orientate.student.profile.ProfileFragment;
import com.erastus.orientate.student.web.WebActivity;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class StudentNavActivity extends AppCompatActivity implements ParentActivityImpl {
    public static final String TAG = "StudentNavActivity";
    private DrawerLayout mStudentNavDrawerLayout;
    private Toolbar mToolbar;
    private StudentNavViewModel mViewModel;
    private NavigationView mNavigationView;
    private View mHeaderView;
    private View mRootView;
    private ImageView mInstitutionProfilePictureImageView;
    private RatingBar mOrientationRatingBar;
    private TextView mOrientationTitleTextView;
    private TextView mOrientationWelcomeMessageTitleTextView;
    private TextView mInstitutionPhysicalAddressTextView;
    private LinearLayout mRatingBarLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityStudentNavBinding studentNavBinding = ActivityStudentNavBinding.inflate(getLayoutInflater());
        setContentView(studentNavBinding.getRoot());

        mRootView = studentNavBinding.getRoot();
        mInstitutionProfilePictureImageView = studentNavBinding.includeAppMain.imageViewSchoolProfilePicture;
        mOrientationRatingBar = studentNavBinding.includeAppMain.ratingBarOrientation;
        mOrientationTitleTextView = studentNavBinding.includeAppMain.textViewOrientationTitle;
        mOrientationWelcomeMessageTitleTextView = studentNavBinding.includeAppMain.textViewOrientationWelcomeMessage;
        mInstitutionPhysicalAddressTextView = studentNavBinding.includeAppMain.textViewPhysicalAddress;
        mRatingBarLinearLayout = studentNavBinding.includeAppMain.layoutRatingBar;


        mViewModel = new ViewModelProvider(this).get(StudentNavViewModel.class);

        mToolbar = findViewById(R.id.toolbar_student_nav);
        setSupportActionBar(mToolbar);
        setTitle(R.string.app_name);
        mStudentNavDrawerLayout = findViewById(R.id.drawer_layout_student);
        mNavigationView = (NavigationView) mStudentNavDrawerLayout.findViewById(R.id.nav_view_student);
        mHeaderView = mNavigationView.inflateHeaderView(R.layout.nav_student_header);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mStudentNavDrawerLayout,
                mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mStudentNavDrawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_notes_24);
        setupDrawerContent(studentNavBinding.navViewStudent);
        setUpObservers(mViewModel);
    }

    private void showRatingsMaterialDialog(float voteAverage, int numVotes) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.rate)
                .customView(R.layout.layout_rating_bar, true)
                .contentColor(getColor(R.color.md_blue_grey_600))
                .positiveText(R.string.rate)
                .negativeText(android.R.string.cancel)
                .backgroundColor(getColor(R.color.white))
                .positiveColor(getColor(R.color.colorPrimaryDark))
                .onPositive((dialog, which) -> {
                    float newRating = ((RatingBar)
                            dialog.getCustomView()
                                    .findViewById(R.id.rating_bar_orientation_dialog))
                            .getRating();
                    mViewModel.notifySelectedRating(newRating);
                })
                .build();

        View customView = materialDialog.getCustomView();
        TextView tv = customView.findViewById(R.id.text_view_num_votes);
        tv.setText(getString(R.string.string_rate, voteAverage, numVotes));
        final RatingBar ratingBar = customView.findViewById(R.id.rating_bar_orientation_dialog);
        ratingBar.setRating(mOrientationRatingBar.getRating());
        materialDialog.show();
        Log.d(TAG, "showRatingsMaterialDialog: ");
    }

    private void setUpObservers(StudentNavViewModel viewModel) {
        viewModel.getActionBarStatus().observe(this, actionBarStatus -> {
            if (actionBarStatus == null) {
                return;
            } if (actionBarStatus.actionBarTitle != null) {
                getSupportActionBar().setTitle(actionBarStatus.actionBarTitle);
            }
            if (!actionBarStatus.isShowActionBar()) {
               getSupportActionBar().hide();
            }
        });

        viewModel.getStudentInstitution().observe(this, institutionSimpleState -> {
            if (institutionSimpleState == null) {
                return;
            }
            if (institutionSimpleState.getData() != null) {
                Institution institution = institutionSimpleState.getData();
                TextView institutionName = mHeaderView.findViewById(R.id.text_view_institution_name);
                TextView institutionLocation = mHeaderView.findViewById(R.id.text_view_institution_location);
                institutionName.setText(institution.getInstitutionName());
                institutionLocation.setText(institution.getLocation());
                mInstitutionPhysicalAddressTextView.setText(institution.getPhysicalAddress());
            }
            if (institutionSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(institutionSimpleState.getErrorMessage());
            }
        });

        viewModel.getStudentOrientation().observe(this, orientationSimpleState -> {
            if (orientationSimpleState == null) {
                return;
            }
            if (orientationSimpleState.getData() != null) {
                fillOrientationObjectData(orientationSimpleState.getData());
            }
            if (orientationSimpleState.getErrorMessage() != null) {
                showErrorSnackBar(orientationSimpleState.getErrorMessage());
            }
            if (orientationSimpleState.getInternalErrorOccurred() != null) {
                showErrorSnackBar("Internal error occurred");
            }
        });
    }

    private void fillOrientationObjectData(Orientation orientation) {
        mOrientationTitleTextView.setText(orientation.getTitle());
        setTitle(orientation.getTitle());
        mOrientationWelcomeMessageTitleTextView.setText(orientation.getWelcomeMessage());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);

        circularProgressDrawable.start();
        Glide.with(this)
                .load(mViewModel.getStudentInstitution().getValue().getData().getProfileImageUrl())
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .into(mInstitutionProfilePictureImageView);
        mOrientationRatingBar.setRating((float) orientation.getVoteAverage());

        mRatingBarLinearLayout.setOnClickListener(view -> {
            Log.d(TAG, "onClick: rating bar clicked");
            showRatingsMaterialDialog((float) orientation.getVoteAverage(), orientation.getNumVotes());
        });
    }

    private void showErrorSnackBar(String message) {
        Snackbar.make(mRootView, message, BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.colorPrimaryDark))
                .setTextColor(getColor(R.color.md_red_400))
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_nav, menu);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        if (menuItem.getItemId() == R.id.nav_student_chats) {
            startActivity(new Intent(this, ChatActivity.class));
            return;
        }
        Fragment fragment = null;
        Class<? extends Fragment> fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_student_events:
                fragmentClass = EventFragment.class;
                break;
            case R.id.nav_helpful_information:
                fragmentClass = InfoFragment.class;
                break;
            case R.id.nav_web:
                startActivity(new Intent(this, WebActivity.class));
                return;
            default:
                fragmentClass = AnnouncementFragment.class;
        }

        try {
            fragment = fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "Exception", e);
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        // Close the navigation drawer
        mStudentNavDrawerLayout.closeDrawer(GravityCompat.START, false);
        mStudentNavDrawerLayout.closeDrawers();
        fragmentManager.beginTransaction().replace(R.id.frame_layout_student_content, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title

        setTitle(menuItem.getTitle());
    }

    @Override
    public void onBackPressed() {
        if (mStudentNavDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mStudentNavDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count != 0) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_go_to_profile) {
            // go to the profile fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout_student_content,
                            new ProfileFragment())
                    .addToBackStack(null)
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setSubtitle(String subtitle) {

    }

    @Override
    public void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_student_content,
                        fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void enableBackButton(boolean enable) {

    }

    @Override
    public void backPress() {

    }

    @Override
    public void hideToolbar(boolean hide) {

    }

    @Override
    public void setTabsVisibility(int visibility) {

    }

    @Override
    public void setToolbar(Toolbar toolbar) {

    }
}