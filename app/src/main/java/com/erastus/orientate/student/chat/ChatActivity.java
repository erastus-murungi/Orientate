package com.erastus.orientate.student.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.erastus.orientate.R;
import com.erastus.orientate.student.chat.connections.ConnectionsFragment;
import com.erastus.orientate.student.chat.main.SectionsPagerAdapter;
import com.erastus.orientate.utils.ParentActivityImpl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

public class ChatActivity extends AppCompatActivity implements ParentActivityImpl {
    private static final String TAG = "ChatActivity";
    private static final int[] TAB_TITLES = new int[]{R.string.chats, R.string.connections};
    private ActionBar mActionBar;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager_chats);

        Toolbar toolbar = findViewById(R.id.toolbar_chat_nav);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewPager.setAdapter(sectionsPagerAdapter);
        mTabLayout = findViewById(R.id.tab_layout_chats);

        new TabLayoutMediator(mTabLayout, viewPager, (tab, position) -> tab.setText(TAB_TITLES[position])).attach();

        FloatingActionButton fab = findViewById(R.id.fab_compose);

        fab.setOnClickListener(view -> Snackbar.make(view, "Compose Message", Snackbar.LENGTH_LONG)
                .setAction("Compose", view1 -> viewPager.setCurrentItem(1)).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void setTitle(String title) {
        mActionBar.setTitle(title);
    }

    @Override
    public void setSubtitle(String subtitle) {

    }

    @Override
    public void addFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        setupAnimations(ft);
        ft.replace(R.id.frame_layout_chats, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void enableBackButton(boolean enable) {

    }

    @Override
    public void backPress() {

    }

    @Override
    public void hideToolbar(boolean hide){
        if (mActionBar != null) {
            if (hide) {
                mActionBar.hide();
            } else {
                mActionBar.show();
            }
        } else {
            Log.e(TAG, "setToolbarVisibility: no action bar");
        }
    }

    @Override
    public void setTabsVisibility(int visibility) {
        mTabLayout.setVisibility(visibility);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }


    private void setupAnimations(FragmentTransaction ft) {
        /*ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim
                .exit_to_right);*/
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideToolbar(false);
    }
}