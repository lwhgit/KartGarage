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
    private final String RECORD_URL = "http://kart.nexon.com/Garage/Record?strRiderID=";
    private final String REPLAY_URL = "http://kart.nexon.com/Garage/Replay?strRiderID=";

    private String name = null;

    private MainParseTask mainParseTask = null;
    private ItemParseTask itemParseTask = null;
    private EmblemParseTask emblemParseTask = null;
    private RecordParseTask recordParseTask = null;

    private RiderDataReceiveListener riderDataReceiveListener = null;
    private ItemDataReceiveListener itemDataReceiveListener = null;
    private EmblemDataReceiveListener emblemDataReceiveListener = null;
    private RecordDataReceiveListener recordDataReceiveListener = null;

    private Context context = null;

    public GarageParser(Context context, String name) {
        try {
            this.context = context;
            this.name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setRiderDataReceiveListener(RiderDataReceiveListener riderDataReceiveListener) {
        this.riderDataReceiveListener = riderDataReceiveListener;
    }

    public void setItemDataReceiveListener(ItemDataReceiveListener itemDataReceiveListener) {
        this.itemDataReceiveListener = itemDataReceiveListener;
    }

    public void setEmblemDataReceiveListener(EmblemDataReceiveListener emblemDataReceiveListener) {
        this.emblemDataReceiveListener = emblemDataReceiveListener;
    }

    public void setRecordDataReceiveListener(RecordDataReceiveListener recordDataReceiveListener) {
        this.recordDataReceiveListener = recordDataReceiveListener;
    }

    public void parseMain() {
        mainParseTask = new MainParseTask();
        mainParseTask.execute(MAIN_URL + name);
    }

    public void parseItem() {
        itemParseTask = new ItemParseTask();
        itemParseTask.execute(ITEM_URL + name);
    }

    public void parseEmblem() {
        emblemParseTask = new EmblemParseTask();
        emblemParseTask.execute(EMBLEM_URL + name);
    }

    public void parseRecord() {
        recordParseTask = new RecordParseTask();
        recordParseTask.execute(RECORD_URL + name);
    }

    public void stopTask() {
        if (mainParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            mainParseTask.cancel(true);
        }
        if (itemParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            itemParseTask.cancel(true);
        }
        if (emblemParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            emblemParseTask.cancel(true);
        }
        if (recordParseTask.getStatus() == AsyncTask.Status.RUNNING) {
            recordParseTask.cancel(true);
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

        public void setName(String name) {
            this.name = name;
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

    public class EmblemData {
        private Bitmap image = null;
        private String name = null;

        public EmblemData(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getName() {
            return name;
        }
    }

    public class RecordData {
        private String kind = null;
        private String odds = null;
        private String rp = null;

        public RecordData(String kind, String odds, String rp) {
            this.kind = kind;
            this.odds = odds;
            this.rp = rp;
        }

        public String getKind() {
            return kind;
        }

        public String getOdds() {
            return odds;
        }

        public String getRp() {
            return rp;
        }
    }

    public class GrandPrixData {
        private String name = null;
        private String point = null;
        private String ranking = null;

        public GrandPrixData(String name, String point, String ranking) {
            this.name = name;
            this.point = point;
            this.ranking = ranking;
        }

        public String getName() {
            return name;
        }

        public String getPoint() {
            return point;
        }

        public String getRanking() {
            return ranking;
        }
    }
    
    public class TimeAttackData {
        private String name = null;
        private String ranking = null;
        private String kart = null;
        private String record = null;
        
        public TimeAttackData(String name, String ranking, String kart, String record) {
            this.name = name;
            this.ranking = ranking;
            this.kart = kart;
            this.record = record;
        }
    
        public String getName() {
            return name;
        }
    
        public String getRanking() {
            return ranking;
        }
    
        public String getKart() {
            return kart;
        }
    
        public String getRecord() {
            return record;
        }
    }

    private class MainParseTask extends AsyncTask<String, RiderData, Integer> {
        @Override
        protected Integer doInBackground(final String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(RiderData... values) {
            if (riderDataReceiveListener != null) {
                riderDataReceiveListener.onRiderDataReceive(values[0]);
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
            try {
                Document document = Jsoup.connect(params[0]).get();
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
                for (int i = 0; i < elements.size(); i++) {
                    if (isCancelled()) {
                        return null;
                    }

                    Element element = elements.get(i);
                    String data = element.select("span.ImgItem img").attr("onclick");
                    if (data.isEmpty()) {
                        continue;
                    }
                    data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                    ItemData itemData = processData(data);

                    if (itemData.getName().isEmpty()) {
                        String name = element.select("span.TxtItem").text();
                        itemData.setName(name);
                    }
                    items.add(itemData);
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (itemDataReceiveListener != null) {
                            itemDataReceiveListener.onRepresentationItemReceive(items);
                        }
                    }
                });

                for (int page = 1; page > 0; page++) {
                    document = Jsoup.connect(params[0] + "&page=" + page).get();
                    elements = document.select("li.ItemList1");
                    for (int i = 0; i < elements.size(); i++) {
                        if (isCancelled()) {
                            return null;
                        }

                        Element element = elements.get(i);
                        String data = element.select("span.ImgItem a").attr("onclick");
                        if (data.isEmpty()) {
                            page = -1;
                            break;
                        }
                        data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                        ItemData itemData = processData(data);

                        if (itemData.getName().isEmpty()) {
                            String name = element.select("span.TxtItem").text();
                            itemData.setName(name);
                        }

                        publishProgress(itemData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        private ItemData processData(String data) throws IOException {
            String str = Jsoup.connect("http://kart.nexon.com/Garage/ItemDetail?Info=" + data).get().text();
            String[] split = str.split("'");

            return new ItemData(getBitmapFromURL(split[11]), split[3], split[5]);
        }
    }

    private class EmblemParseTask extends AsyncTask<String, EmblemData, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                Elements elements = document.select("div#CntEmPro dl");

                final String acquisition = elements.get(0).select("span").text();
                final String ranking = elements.get(1).select("span").text();
                final String level = elements.get(2).select("span").text();
                final String ep = elements.get(3).select("span").text();
                final String quest = elements.get(4).select("dd").text();

                Log.i("Emb", "Acc: " + acquisition);
                Log.i("Emb", "Rank: " + ranking);
                Log.i("Emb", "Lv: " + level);
                Log.i("Emb", "Ep: " + ep);
                Log.i("Emb", "Q: " + quest);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (emblemDataReceiveListener != null) {
                            emblemDataReceiveListener.onCountReceive(acquisition, ranking, level, ep, quest);
                        }
                    }
                });

                for (int ced = 1;ced <= 5;ced ++) {
                    for (int page = 1;page > 0;page ++) {
                        document = Jsoup.connect(params[0] + "&ced=" + ced + "&page=" + page).get();
                        elements = document.select("li.EmList");
                        for (int i = 0;i < 10;i ++) {
                            if (isCancelled()) {
                                return null;
                            }
                            Element element = elements.get(i).select("img").first();
                            String imageUrl = element.attr("src");
                            String name = element.attr("alt");

                            if (imageUrl.equals("http://s.nx.com/S2/game/kart/Camp/image/profile_kart/img_emblem_none.gif")) {
                                page = -1;
                                break;
                            }
                            Log.i("Emb", "URL: " + imageUrl);
                            Log.i("Emb", "Name: " + name);

                            publishProgress(new EmblemData(getBitmapFromURL(imageUrl), name));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(EmblemData... values) {
            if (emblemDataReceiveListener != null) {
                emblemDataReceiveListener.onEmblemDataReceive(values[0]);
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

    private class RecordParseTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                Elements elements = document.select("table.RecordL tbody tr");
                final RecordData[] recordDataList = new RecordData[3];
                for (int i = 0;i < 3;i ++) {
                    Element element = elements.get(i);
                    String kind = element.select("td.RecordL1").text();
                    String odds = element.select("td.RecordL2").text();
                    String rp = element.select("td.RecordL3").text();

                    Log.i("Record", "K: " + kind);
                    Log.i("Record", "O: " + odds);
                    Log.i("Record", "R: " + rp);
    
                    recordDataList[i] = new RecordData(kind, odds, rp);
                }
                
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (recordDataReceiveListener != null) {
                            recordDataReceiveListener.onOddsReceive(recordDataList);
                        }
                    }
                });
                
                final ArrayList<GrandPrixData> grandPrixDataList = new ArrayList<>();
                
                for (int gpage = 1;gpage > 0;gpage ++) {
                    document = Jsoup.connect(params[0] + "&gpage=" + gpage).get();
                    elements = document.select("table.CntGpL tbody tr");
                    int size = elements.size();
                    for (int i = 0;i < size;i ++) {
                        Element element = elements.get(i);
                        String name = element.select("td.CntGpL1").text();
                        String point = element.select("td.CntGpL2").text();
                        String ranking = element.select("td.CntGpL3").text();
                        
                        Log.i("Record", "N: " + name);
                        Log.i("Record", "P: " + point);
                        Log.i("Record", "R: " + ranking);
    
                        grandPrixDataList.add(new GrandPrixData(name, point, ranking));
                    }
                    
                    if(size < 5) {
                        break;
                    }
                }
    
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (recordDataReceiveListener != null) {
                            recordDataReceiveListener.onGrandPrixReceive(grandPrixDataList);
                        }
                    }
                });
                
                final ArrayList<TimeAttackData>[] timeAttackLists = new ArrayList[4];
                for (int lc = 0;lc <= 3;lc ++) {
                    timeAttackLists[lc] = new ArrayList<>();
                    for (int tpage = 1;tpage > 0;tpage ++) {
                        document = Jsoup.connect(params[0] + "&lc=" + lc + "&tpage=" + tpage).get();
                        elements = document.select("div.CntTime2Sec tr");
                        int size = elements.size();
    
                        Log.i("Time Attack page: " + tpage, "--------------");
                        for (int i = 0;i < size;i ++) {
                            Element element = elements.get(i);
                            String name = element.select("th.CntTime2L1").text();
                            String ranking = element.select("td.CntTime2L2").text();
                            String kart = element.select("td.CntTime2L3").text();
                            String record = element.select("td.CntTime2L4").text();
                            Log.i("Time Attack lc: " + lc,"Name: " + name);
                            Log.i("Time Attack lc: " + lc,"Ranking: " + ranking);
                            Log.i("Time Attack lc: " + lc,"Kart: " + kart);
                            Log.i("Time Attack lc: " + lc,"Record: " + record);
                            timeAttackLists[lc].add(new TimeAttackData(name, ranking, kart, record));
                        }
                        
                        if (size < 3) {
                            break;
                        }
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (recordDataReceiveListener != null) {
                            recordDataReceiveListener.onTimeAttackReceive(timeAttackLists);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
    }

    public interface ItemDataReceiveListener {
        public void onItemCountReceive(int characterCount, int kartCount, int wearingCount, int embellishmentCount, int etcCount);
        public void onRepresentationItemReceive(ArrayList<ItemData> items);
        public void onAllItemReceive(ItemData item);
    }

    public interface EmblemDataReceiveListener {
        public void onCountReceive(String acquisition, String ranking, String level, String ep, String quest);
        public void onEmblemDataReceive(EmblemData emblem);
    }

    public interface RecordDataReceiveListener {
        public void onOddsReceive(RecordData[] data);
        public void onGrandPrixReceive(ArrayList<GrandPrixData> dataList);
        public void onTimeAttackReceive(ArrayList<TimeAttackData>[] dataList);
    }
}
