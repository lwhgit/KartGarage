package com.lwh8762.kartgarage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by W on 2017-07-17.
 */

public class GarageParser {
    private final String MAIN_URL = "http://kart.nexon.com/Garage/Main?strRiderID=";
    private final String ITEM_URL = "http://kart.nexon.com/Garage/Item?strRiderID=";
    private final String EMBLEM_URL = "http://kart.nexon.com/Garage/Emblem?strRiderID=";
    private final String[] RECORD_URL = new String[] {
            "http://kart.nexon.com/Garage/Record?lc=3&strRiderID=", // S0
            "http://kart.nexon.com/Garage/Record?lc=0&strRiderID=", // S1
            "http://kart.nexon.com/Garage/Record?lc=1&strRiderID=", // S2
            "http://kart.nexon.com/Garage/Record?lc=2&strRiderID="  // S3
    };
    private final String REPLAY_URL = "http://kart.nexon.com/Garage/Replay?strRiderID=";

    private String name = null;

    private HtmlLoader htmlLoader = null;

    private MainParseTask mainParseTask = null;
    private ItemParseTask itemParseTask = null;

    private RiderDataReceiveListener riderDataReceiveListener = null;
    private ItemDataReceiveListener itemDataReceiveListener = null;

    private Context context = null;

    public GarageParser(Context context, String name) {
        try {
            this.context = context;

            this.name = URLEncoder.encode(name, "utf-8");

            htmlLoader = new HtmlLoader(context);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public HtmlLoader getHtmlLoader() {
        return htmlLoader;
    }

    public void setRiderDataReceiveListener(RiderDataReceiveListener riderDataReceiveListener) {
        this.riderDataReceiveListener = riderDataReceiveListener;
    }

    public void setItemDataReceiveListener(ItemDataReceiveListener itemDataReceiveListener) {
        this.itemDataReceiveListener = itemDataReceiveListener;
    }

    public void parseMain() {
        mainParseTask = new MainParseTask();
        mainParseTask.execute(name);
    }

    public void parseItem() {
        itemParseTask = new ItemParseTask();
        itemParseTask.execute(name);
    }

    public void stopTask() {
        if (mainParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            mainParseTask.cancel(true);
        }
        if (itemParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            itemParseTask.cancel(true);
        }
    }

    private static class HtmlLoader extends WebView {
        private HtmlLoadListener htmlLoadListener = null;

        public HtmlLoader(Context context) {
            super(context);
            getSettings().setJavaScriptEnabled(true);
            setWebViewClient(new WebViewClient() {


                @Override
                public void onPageFinished(WebView view, String url) {
                    view.loadUrl("javascript:window.AndroidInterface.receiveHtml(document.getElementsByTagName('html')[0].innerHTML);");
                }
            });
            addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void receiveHtml(String html) {
                    if (htmlLoadListener != null) {
                        htmlLoadListener.onHtmlLoad(html);
                    }
                }
            }, "AndroidInterface");
        }

        public void requestGetHtml(String url) {
            loadUrl(url);
        }

        public void setHtmlLoadListener(HtmlLoadListener htmlLoadListener) {
            this.htmlLoadListener = htmlLoadListener;
        }

        interface HtmlLoadListener {
            public void onHtmlLoad(String html);
        }
    }

    public class RiderData {
        private Bitmap riderImage = null;
        private String riderName = null;
        private Bitmap gloveImage = null;
        private Bitmap guildImage = null;
        private String guildName = null;

        public RiderData(Bitmap riderImage, String riderName, Bitmap gloveImage, Bitmap guildImage, String guildName) {
            this.riderImage = riderImage;
            this.riderName = riderName;
            this.gloveImage = gloveImage;
            this.guildImage = guildImage;
            this.guildName = guildName;
        }

        public Bitmap getRiderImage() {
            return riderImage;
        }

        public String getRiderName() {
            return riderName;
        }

        public Bitmap getGloveImage() {
            return gloveImage;
        }

        public Bitmap getGuildImage() {
            return guildImage;
        }

        public String getGuildName() {
            return guildName;
        }
    }

    public class ItemData {
        private Bitmap image = null;
        private String name = null;
        private String count = null;

        public ItemData(Bitmap image, String name, String count) {
            this.image = image;
            this.name = name;
            this.count = count;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public String getCount() {
            return count;
        }
    }

    private class MainParseTask extends AsyncTask<String, RiderData, Integer> {
        @Override
        protected Integer doInBackground(final String... params) {
            final String[] receivedHtml = new String[1];
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    htmlLoader.requestGetHtml(MAIN_URL + params[0]);
                    htmlLoader.setHtmlLoadListener(new HtmlLoader.HtmlLoadListener() {
                        @Override
                        public void onHtmlLoad(String html) {
                            receivedHtml[0] = html;
                        }
                    });
                }
            });

            while(receivedHtml[0] == null) {
                if (isCancelled()) {
                    return null;
                }
            }

            Document document = Jsoup.parse(receivedHtml[0]);
            String riderImageUrl = document.select("span#RiderImg > img").attr("src");
            String riderName = document.select("span#RiderName").text();
            String gloveImageUrl = document.select("div#GloveImg > img").attr("src");
            String guildImageUrl = document.select("span#GuildImg > img").attr("src");
            String guildName = document.select("span#GuildName").text();

            Log.i("RiderData", "Rider Image URL: " + riderImageUrl);
            Log.i("RiderData", "Rider Name: " + riderName);
            Log.i("RiderData", "Glove Image URL: " + gloveImageUrl);
            Log.i("RiderData", "Guild Image URL: " + guildImageUrl);
            Log.i("RiderData", "Guild Name: " + guildName);

            RiderData riderData = new RiderData(
                    getBitmapFromURL(riderImageUrl),
                    riderName,
                    getBitmapFromURL(gloveImageUrl),
                    getBitmapFromURL(guildImageUrl),
                    guildName);

            publishProgress(riderData);
            return null;
        }

        @Override
        protected void onProgressUpdate(RiderData... values) {
            if (riderDataReceiveListener != null) {
                riderDataReceiveListener.onRiderDataReceive(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (riderDataReceiveListener != null) {
                riderDataReceiveListener.onParsingEnd();
            }
        }

        private Bitmap getBitmapFromURL(String url) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class ItemParseTask extends AsyncTask<String, ItemData, Integer> {
        @Override
        protected Integer doInBackground(final String... params) {
            final String[] receivedHtml = new String[1];
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    htmlLoader.requestGetHtml(ITEM_URL + params[0]);
                    htmlLoader.setHtmlLoadListener(new HtmlLoader.HtmlLoadListener() {
                        @Override
                        public void onHtmlLoad(String html) {
                            receivedHtml[0] = html;
                        }
                    });
                }
            });

            while(receivedHtml[0] == null) {
                if (isCancelled()) {
                    return null;
                }
            }

            Document document = Jsoup.parse(receivedHtml[0]);
            Elements elements = document.select("div#CntItemDet dl");
            final int characterCount = getCountFromString(elements.eq(0).select("dd").text());
            final int kartCount = getCountFromString(elements.eq(1).select("dd").text());
            final int wearingCount = getCountFromString(elements.eq(2).select("dd").text());
            final int embellishmentCount = getCountFromString(elements.eq(3).select("dd").text());
            final int etcCount = getCountFromString(elements.eq(4).select("dd").text());

            Log.i("Count", "Character : " + characterCount);
            Log.i("Count", "Kart : " + kartCount);
            Log.i("Count", "Wearing : " + wearingCount);
            Log.i("Count", "Embellishment : " + embellishmentCount);
            Log.i("Count", "Etc : " + etcCount);

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (itemDataReceiveListener != null) {
                        itemDataReceiveListener.onItemCountReceive(characterCount, kartCount, wearingCount, embellishmentCount, etcCount);
                    }
                }
            });

            final ArrayList<ItemData> items = new ArrayList<>();
            elements = document.select("li.ItemList");
            for (int i = 0;i < elements.size();i ++) {
                if (isCancelled()) {
                    return null;
                }

                Element element = elements.get(i);
                String imageUrl = element.select("span.ImgItem img").attr("src");
                String name = element.select("span.TxtItemDate").text();
                String count = element.select("span.TxtItemInfo").text();
                Log.i("Representation", "url : " + imageUrl);
                Log.i("Representation", "name : " + name);
                Log.i("Representation", "count : " + count);
                items.add(new ItemData(getBitmapFromURL(imageUrl), name, count));
            }

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (itemDataReceiveListener != null) {
                        itemDataReceiveListener.onRepresentationItemReceive(items);
                    }
                }
            });

            for (int page = 1;page > 0;page ++) {
                final int tmp = page;
                receivedHtml[0] = null;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htmlLoader.requestGetHtml(ITEM_URL + params[0] + "&page=" + tmp);
                        htmlLoader.setHtmlLoadListener(new HtmlLoader.HtmlLoadListener() {
                            @Override
                            public void onHtmlLoad(String html) {
                                receivedHtml[0] = html;
                            }
                        });
                    }
                });

                while(receivedHtml[0] == null) {
                    if (isCancelled()) {
                        return null;
                    }
                }

                document = Jsoup.parse(receivedHtml[0]);
                elements = document.select("li.ItemList1");
                for (int i = 0;i < elements.size();i ++) {
                    if (isCancelled()) {
                        return null;
                    }
                    Element element = elements.get(i);
                    String imageUrl = element.select("span.ImgItem img").attr("src");
                    String name = element.select("span.TxtItem").text();
                    Log.i("ITEM", "URL : " + imageUrl);
                    Log.i("ITEM", "NAME : " + name);

                    if (name.isEmpty()) {
                        page = -1;
                        break;
                    }

                    publishProgress(new ItemData(getBitmapFromURL(imageUrl), name, ""));
                }
            }
            itemDataReceiveListener.onParsingEnd();
            return null;
        }

        @Override
        protected void onProgressUpdate(ItemData... values) {
            if (itemDataReceiveListener != null) {
                itemDataReceiveListener.onAllItemReceive(values[0]);
            }
        }

        private int getCountFromString(String str) {
            return Integer.parseInt(str.substring(0, str.length() - 1));
        }

        private Bitmap getBitmapFromURL(String url) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public interface RiderDataReceiveListener {
        public void onRiderDataReceive(RiderData riderData);
        public void onParsingEnd();
    }

    public interface ItemDataReceiveListener {
        public void onItemCountReceive(int characterCount, int kartCount, int wearingCount, int embellishmentCount, int etcCount);
        public void onRepresentationItemReceive(ArrayList<ItemData> items);
        public void onAllItemReceive(ItemData data);
        public void onParsingEnd();
    }
}