package com.lwh8762.kartgarage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textView = null;
    private GarageParser garageParser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        garageParser = new GarageParser(this, "인디벨");
        garageParser.setRiderDataReceiveListener(new GarageParser.RiderDataReceiveListener() {
            @Override
            public void onRiderDataReceive(GarageParser.RiderData riderData) {
                textView.append("\nName : " + riderData.getRiderName() + "\nGuild Name " + riderData.getGuildName());
            }
        });
        garageParser.setItemDataReceiveListener(new GarageParser.ItemDataReceiveListener() {
            @Override
            public void onItemCountReceive(int characterCount, int kartCount, int wearingCount, int embellishmentCount, int etcCount) {
                textView.append("\nItem Count");
                textView.append("\nCharacter Count : " + characterCount + "\nKart Count : " + kartCount + "\nWearing Count : " + wearingCount + "\nEmbel Count : " + embellishmentCount + "\nEtc Count : " + etcCount);
            }

            @Override
            public void onRepresentationItemReceive(ArrayList<GarageParser.ItemData> items) {
                textView.append("\nRepresentation");
                for (GarageParser.ItemData item : items)
                    textView.append("\nItem Name : " + item.getName() + "\n\tItem Count : " + item.getCount());
            }

            @Override
            public void onAllItemReceive(GarageParser.ItemData item) {
                textView.append("\nItem Name : " + item.getName() + "\n\tItem Count : " + item.getCount());
            }
        });
        garageParser.parseMain();
        garageParser.parseItem();
    }

    @Override
    protected void onDestroy() {
        garageParser.stopTask();
        super.onDestroy();
    }
}
