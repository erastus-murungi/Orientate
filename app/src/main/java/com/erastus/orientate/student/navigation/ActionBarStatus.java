package com.erastus.orientate.student.navigation;

import androidx.annotation.Nullable;

public class ActionBarStatus {
    @Nullable String actionBarTitle;
    boolean showActionBar;

    public ActionBarStatus(@Nullable String actionBarTitle, boolean showActionBar) {
        this.actionBarTitle = actionBarTitle;
        this.showActionBar = showActionBar;
    }

    @Nullable
    public String getActionBarTitle() {
        return actionBarTitle;
    }

    public boolean isShowActionBar() {
        return showActionBar;
    }
}
