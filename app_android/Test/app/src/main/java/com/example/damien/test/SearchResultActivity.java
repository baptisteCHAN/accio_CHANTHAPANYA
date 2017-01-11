package com.example.damien.test;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Damien on 11/01/2017.
 */

public class SearchResultActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_result);
        /*int count = 0;
        Resources res = getResources();
        String tripsFound = res.getQuantityString(R.plurals.search_result, count, count);
        TextView txt = (TextView) findViewById(R.id.result_count);
        txt.setText(tripsFound);*/

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandList);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

    }

    /*
    * Preparing the list data
    */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Trajet 1 : (16:35) --> (17:35)");
        listDataHeader.add("Trajet 2 : (16:40) --> (17:50)");
        listDataHeader.add("Trajet 3 : (16:35) --> (17:59)");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
