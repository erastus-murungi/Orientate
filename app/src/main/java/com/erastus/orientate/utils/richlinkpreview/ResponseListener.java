package com.erastus.orientate.utils.richlinkpreview;

import com.erastus.orientate.utils.richlinkpreview.models.LinkData;

public interface ResponseListener {

    void onData(LinkData linkData);

    void onError(Exception e);
}
