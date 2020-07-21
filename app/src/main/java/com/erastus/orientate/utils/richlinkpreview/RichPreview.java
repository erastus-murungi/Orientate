package com.erastus.orientate.utils.richlinkpreview;

import android.webkit.URLUtil;

import com.erastus.orientate.utils.TaskRunner;
import com.erastus.orientate.utils.richlinkpreview.models.LinkData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;


public class RichPreview {

    ResponseListener responseListener;
    String url;

    public RichPreview(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void getPreview(String url){
        this.url = url;
        TaskRunner.getInstance().executeAsync(new GetDataTask(),
                linkData -> responseListener.onData(linkData));
    }

    class GetDataTask implements Callable<LinkData> {
        @Override
        public LinkData call() {
            LinkData linkData = new LinkData();
            Document doc;
            try {
                doc = Jsoup.connect(url)
                        .timeout(30000)
                        .get();

                Elements elements = doc.getElementsByTag("meta");

                // getTitle doc.select("meta[property=og:title]")
                String title = doc.select("meta[property=og:title]").attr("content");

                if(title == null || title.isEmpty()) {
                    title = doc.title();
                }
                linkData.setTitle(title);

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
                linkData.setDescription(description);


                // getMediaType
                Elements mediaTypes = doc.select("meta[name=medium]");
                String type = "";
                if (mediaTypes.size() > 0) {
                    String media = mediaTypes.attr("content");

                    type = media.equals("image") ? "photo" : media;
                } else {
                    type = doc.select("meta[property=og:type]").attr("content");
                }
                linkData.setMediaType(type);


                //getImages
                Elements imageElements = doc.select("meta[property=og:image]");
                if(imageElements.size() > 0) {
                    String image = imageElements.attr("content");
                    if(!image.isEmpty()) {
                        linkData.setImageUrl(resolveURL(url, image));
                    }
                }
                if(linkData.getImageUrl().isEmpty()) {
                    String src = doc.select("link[rel=image_src]").attr("href");
                    if (!src.isEmpty()) {
                        linkData.setImageUrl(resolveURL(url, src));
                    } else {
                        src = doc.select("link[rel=apple-touch-icon]").attr("href");
                        if(!src.isEmpty()) {
                            linkData.setImageUrl(resolveURL(url, src));
                            linkData.setFavicon(resolveURL(url, src));
                        } else {
                            src = doc.select("link[rel=icon]").attr("href");
                            if(!src.isEmpty()) {
                                linkData.setImageUrl(resolveURL(url, src));
                                linkData.setFavicon(resolveURL(url, src));
                            }
                        }
                    }
                }

                //Favicon
                String src = doc.select("link[rel=apple-touch-icon]").attr("href");
                if(!src.isEmpty()) {
                    linkData.setFavicon(resolveURL(url, src));
                } else {
                    src = doc.select("link[rel=icon]").attr("href");
                    if(!src.isEmpty()) {
                        linkData.setFavicon(resolveURL(url, src));
                    }
                }

                for(Element element : elements) {
                    if(element.hasAttr("property")) {
                        String str_property = element.attr("property").toString().trim();
                        if(str_property.equals("og:url")) {
                            linkData.setUrl(element.attr("content").toString());
                        }
                        if(str_property.equals("og:site_name")) {
                            linkData.setSiteName(element.attr("content").toString());
                        }
                    }
                }

                if(linkData.getUrl().equals("") || linkData.getUrl().isEmpty()) {
                    URI uri = null;
                    try {
                        uri = new URI(url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if(url == null) {
                        linkData.setUrl(null);
                    } else {
                        assert uri != null;
                        linkData.setUrl(uri.getHost());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                responseListener.onError(new Exception("No Html Received from " + url + " Check your Internet " + e.getLocalizedMessage()));
            }
            return linkData;
        }
    }

    private String resolveURL(String url, String part) {
        if(URLUtil.isValidUrl(part)) {
            return part;
        } else {
            URI baseUri = null;
            try {
                baseUri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            baseUri = baseUri.resolve(part);
            return baseUri.toString();
        }
    }

}