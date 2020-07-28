package com.erastus.orientate.utils;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public interface ParentActivityImpl {

    void setTitle(String title);

    void setSubtitle(String subtitle);

    void addFragment(Fragment fragment);

    void enableBackButton(boolean enable);

    void backPress();

    void hideToolbar(boolean hide);

    void setTabsVisibility(int visibility);

    void setToolbar(Toolbar toolbar);

}
