package com.erastus.orientate.utils.richlinkpreview;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class UrlSpanNoUnderline extends URLSpan {
    public UrlSpanNoUnderline(String p_Url) {
        super(p_Url);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
    }
}
