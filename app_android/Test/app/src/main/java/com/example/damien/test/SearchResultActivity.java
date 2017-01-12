package com.example.damien.test;

import android.app.DownloadManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Damien on 11/01/2017.
 */

public class SearchResultActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Trip>> listDataChild;
    private static final String urlGetTrajet = "http://192.168.12.79";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

        expListView.setClickable(true);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("test", "CLICCLICCLIC");
                final Trip childTrip = (Trip) listAdapter.getChild(groupPosition, childPosition);
                MapView mMapView = (MapView) findViewById(R.id.map);
                mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                mMapView.setBuiltInZoomControls(true);
                MapController mMapController = (MapController) mMapView.getController();
                mMapController.setZoom(10);

                ArrayList<TripPoint> points = childTrip.getPoints();

                TripPoint departure = points.get(0);
                TripPoint arrival = points.get(points.size() - 1);

                mMapView.getOverlays().clear();

                Marker startMarker = new Marker(mMapView);
                startMarker.setPosition(new GeoPoint(departure.getLattitude(),departure.getLongitude()));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(startMarker);

                Marker arrivalMarker = new Marker(mMapView);
                arrivalMarker.setPosition(new GeoPoint(arrival.getLattitude(),arrival.getLongitude()));
                arrivalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(arrivalMarker);

                GeoPoint gPt = new GeoPoint((departure.getLattitude() + arrival.getLattitude()) / 2, (departure.getLongitude() + arrival.getLongitude()) / 2);
                mMapController.setCenter(gPt);
                return false;
            }
        });

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

    }

    /*
    * Preparing the list data
    */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Trip>>();

        // Adding child data
        listDataHeader.add("Trajet 1 : (16:35) --> (17:35)");
        listDataHeader.add("Trajet 2 : (16:40) --> (17:50)");
        listDataHeader.add("Trajet 3 : (16:35) --> (17:59)");

        // Adding child data
        List<Trip> top250 = new ArrayList<Trip>();
        ArrayList<TripPoint> trip = new ArrayList<TripPoint>();
        trip.add(new TripPoint(49.222833,-0.370879));
        trip.add(new TripPoint(49.213391,-0.375315));
        top250.add(new Trip("Chez Julien", "Chez Damien", trip));


        trip = new ArrayList<TripPoint>();
        trip.add(new TripPoint(49.198736,-0.36414));
        trip.add(new TripPoint(49.186641,-0.366817));
        List<Trip> nowShowing = new ArrayList<Trip>();
        nowShowing.add(new Trip("Chez Julien", "Chez Damien", trip));

        List<Trip> comingSoon = new ArrayList<Trip>();
        comingSoon.add(new Trip("Chez Julien", "Chez Damien", trip));

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    public void onResearch(){
        RequestParams params = new RequestParams();
        invokeWS(params);
    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlGetTrajet, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Enregistrement réussi", Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for(int i=0;i<jsonArray.length();i++){

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error ) {
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),  "  "+error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
