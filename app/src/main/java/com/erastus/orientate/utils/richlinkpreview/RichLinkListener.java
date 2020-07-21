package com.erastus.orientate.utils.richlinkpreview;

import android.view.View;

import com.erastus.orientate.utils.richlinkpreview.models.LinkData;

public interface RichLinkListener {

    void onClicked(View view, LinkData meta);

}
