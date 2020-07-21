package com.erastus.orientate.student.navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentNavBinding;
import com.erastus.orientate.student.announcements.AnnouncementFragment;
import com.erastus.orientate.student.announcements.models.Announcement;
import com.erastus.orientate.student.event.EventFragment;
import com.erastus.orientate.student.info.InfoFragment;
import com.google.android.material.navigation.NavigationView;

public class StudentNavActivity extends AppCompatActivity {
    public static final String TAG = "StudentNavActivity";
    private DrawerLayout mStudentNavDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityStudentNavBinding studentNavBinding = ActivityStudentNavBinding.inflate(getLayoutInflater());
        setContentView(studentNavBinding.getRoot());

        StudentNavViewModel viewModel = new ViewModelProvider(this).get(StudentNavViewModel.class);

        mToolbar = findViewById(R.id.toolbar_student_nav);
        setSupportActionBar(mToolbar);
        mStudentNavDrawerLayout = findViewById(R.id.drawer_layout_student);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mStudentNavDrawerLayout,
                mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mStudentNavDrawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_notes_24);
        setupDrawerContent(studentNavBinding.navViewStudent);
        setUpObservers(viewModel);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_nav, menu);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class<? extends Fragment> fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_student_events:
                fragmentClass = EventFragment.class;
                break;
            case R.id.nav_helpful_information:
                fragmentClass = InfoFragment.class;
                break;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mStudentNavDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mStudentNavDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}