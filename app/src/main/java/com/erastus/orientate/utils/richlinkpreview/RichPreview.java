package com.erastus.orientate.utils.richlinkpreview;

import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.erastus.orientate.utils.richlinkpreview.models.LinkData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by ponna on 16-01-2018.
 */

public class RichPreview {

    LinkData LinkData;
    ResponseListener responseListener;
    String url;

    public RichPreview(ResponseListener responseListener) {
        this.responseListener = responseListener;
        LinkData = new LinkData();
    }

    public void getPreview(String url){
        this.url = url;
        new getData().execute();
    }

    private class getData extends AsyncTask<Void , Void , Void> {


        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url)
                        .timeout(30*1000)
                        .get();

                Elements elements = doc.getElementsByTag("meta");

                // getTitle doc.select("meta[property=og:title]")
                String title = doc.select("meta[property=og:title]").attr("content");

                if(title == null || title.isEmpty()) {
                    title = doc.title();
                }
                LinkData.setTitle(title);

                //getDescription
                String description = doc.select("meta[name=description]").attr("content");
                if (description.isEmpty()) {
                    description = doc.select("meta[name=Description]").attr("content");
                }
                if (description.isEmpty()) {
                    description = doc.select("meta[property=og:description]").attr("content");
                }
                if (description.isEmpty()) {
                    description = "";
                }
                LinkData.setDescription(description);


                // getMediaType
                Elements mediaTypes = doc.select("meta[name=medium]");
                String type = "";
                if (mediaTypes.size() > 0) {
                    String media = mediaTypes.attr("content");

                    type = media.equals("image") ? "photo" : media;
                } else {
                    type = doc.select("meta[property=og:type]").attr("content");
                }
                LinkData.setMediaType(type);


                //getImages
                Elements imageElements = doc.select("meta[property=og:image]");
                if(imageElements.size() > 0) {
                    String image = imageElements.attr("content");
                    if(!image.isEmpty()) {
                        LinkData.setImageUrl(resolveURL(url, image));
                    }
                }
                if(LinkData.getImageUrl().isEmpty()) {
                    String src = doc.select("link[rel=image_src]").attr("href");
                    if (!src.isEmpty()) {
                        LinkData.setImageUrl(resolveURL(url, src));
                    } else {
                        src = doc.select("link[rel=apple-touch-icon]").attr("href");
                        if(!src.isEmpty()) {
                            LinkData.setImageUrl(resolveURL(url, src));
                            LinkData.setFavicon(resolveURL(url, src));
                        } else {
                            src = doc.select("link[rel=icon]").attr("href");
                            if(!src.isEmpty()) {
                                LinkData.setImageUrl(resolveURL(url, src));
                                LinkData.setFavicon(resolveURL(url, src));
                            }
                        }
                    }
                }

                //Favicon
                String src = doc.select("link[rel=apple-touch-icon]").attr("href");
                if(!src.isEmpty()) {
                    LinkData.setFavicon(resolveURL(url, src));
                } else {
                    src = doc.select("link[rel=icon]").attr("href");
                    if(!src.isEmpty()) {
                        LinkData.setFavicon(resolveURL(url, src));
                    }
                }

                for(Element element : elements) {
                    if(element.hasAttr("property")) {
                        String str_property = element.attr("property").toString().trim();
                        if(str_property.equals("og:url")) {
                            LinkData.setUrl(element.attr("content").toString());
                        }
                        if(str_property.equals("og:site_name")) {
                            LinkData.setSiteName(element.attr("content").toString());
                        }
                    }
                }

                if(LinkData.getUrl().equals("") || LinkData.getUrl().isEmpty()) {
                    URI uri = null;
                    try {
                        uri = new URI(url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if(url == null) {
                        LinkData.setUrl(url);
                    } else {
                        LinkData.setUrl(uri.getHost());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                responseListener.onError(new Exception("No Html Received from " + url + " Check your Internet " + e.getLocalizedMessage()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            responseListener.onData(LinkData);
        }
    }

    private String resolveURL(String url, String part) {
        if(URLUtil.isValidUrl(part)) {
            return part;
        } else {
            URI base_uri = null;
            try {
                base_uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            base_uri = base_uri.resolve(part);
            return base_uri.toString();
        }
    }

}