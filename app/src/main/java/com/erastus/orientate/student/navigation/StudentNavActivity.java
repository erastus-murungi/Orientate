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

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityStudentNavBinding;
import com.erastus.orientate.student.announcements.AnnouncementFragment;
import com.erastus.orientate.student.event.EventFragment;
import com.erastus.orientate.student.info.InfoFragment;
import com.google.android.material.navigation.NavigationView;

public class StudentNavActivity extends AppCompatActivity {
    public static final String TAG = "StudentNavActivity";
    private DrawerLayout mStudentNavDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityStudentNavBinding studentNavBinding = ActivityStudentNavBinding.inflate(getLayoutInflater());
        setContentView(studentNavBinding.getRoot());

        final Toolbar toolbar = findViewById(R.id.toolbar_student_nav);
        setSupportActionBar(toolbar);
        mStudentNavDrawerLayout = findViewById(R.id.drawer_layout_student);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mStudentNavDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mStudentNavDrawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_notes_24);
        setupDrawerContent(studentNavBinding.navViewStudent);
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
            case R.id.nav_students_announcements:
                fragmentClass = AnnouncementFragment.class;
                break;
            case R.id.nav_helpful_information:
                fragmentClass = InfoFragment.class;
                break;
            default:
                fragmentClass = ExtraListDialogFragment.class;
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