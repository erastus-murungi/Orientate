package com.erastus.orientate.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class Utils {

    /**
     * Force show softKeyboard.
     */
    public static void forceShow(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Force hide softKeyboard.
     */
    public static void forceHide(@NonNull Activity activity, @NonNull EditText editText) {
        if (activity.getCurrentFocus() == null || !(activity.getCurrentFocus() instanceof EditText)) {
            editText.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static String emphasizeText(String text) {
        return "<b>" + text + "</b>";
    }

    public static String newLine() {
        return "<br>";
    }

    public static void hideKeyboard(View view, Context context) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
