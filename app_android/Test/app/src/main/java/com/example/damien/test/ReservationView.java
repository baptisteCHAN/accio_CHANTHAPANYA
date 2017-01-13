package com.example.damien.test;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ReservationView extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Trip>> listDataChild;
    private Trip currentTrip = null;
    private static final String urlGetTrajet = "http://192.168.12.79:3000/centrectrl/";
    private MapView mMapView;
    private ArrayList<TripPoint> trip;
    private List<Trip> trips;
    private MapController mMapController;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.reservation_view_page);
        /*int count = 0;
        Resources res = getResources();
        String tripsFound = res.getQuantityString(R.plurals.search_result, count, count);
        TextView txt = (TextView) findViewById(R.id.result_count);
        txt.setText(tripsFound);*/

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandList);
        Bundle extras = getIntent().getExtras();
        try {
            JSONArray jsonArray = new JSONArray(extras.getString("jsonPOINTS"));
            for (int i = 0; i < jsonArray.length(); i++) {
                trip = new ArrayList<TripPoint>();
                JSONArray jsonPOINTArray = jsonArray.getJSONArray(i);
                for(int j=0;j<jsonPOINTArray.length();j++) {
                    JSONObject jsonTMP = jsonArray.getJSONObject(i);
                    trip.add(new TripPoint(jsonTMP.getDouble("lat"), jsonTMP.getDouble("lon")));
                }
                trips.add(new Trip("arrivée", "départ", trip, "0", "0"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        // preparing list data REMPLACER ICI PAR UN APPEL A LA BDD
       /* List<Trip> trips = new ArrayList<Trip>();
        ArrayList<TripPoint> trip = new ArrayList<TripPoint>();
        trip.add(new TripPoint(49.222833,-0.370879));
        trip.add(new TripPoint(49.213391,-0.375315));
        trips.add(new Trip("Chez Julien", "Chez Damien", trip, "02:10", "03:15"));


        trip = new ArrayList<TripPoint>();
        trip.add(new TripPoint(49.198736,-0.36414));
        trip.add(new TripPoint(49.186641,-0.366817));
        trips.add(new Trip("Chez Julien", "Chez Damien", trip));

        trips.add(new Trip("Chez Julien", "Chez Damien", trip));
        RequestParams params = new RequestParams();
        invokeWS(params);*/
        prepareListData(trips);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setClickable(true);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final Trip childTrip = (Trip) listAdapter.getChild(groupPosition, childPosition);
                currentTrip = childTrip;

                mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                mMapView.setBuiltInZoomControls(true);
                mMapController.setZoom(15);

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


                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(new GeoPoint(departure.getLattitude(),departure.getLongitude()));
                waypoints.add(new GeoPoint(arrival.getLattitude(),arrival.getLongitude()));

                GraphHopperRoadManager roadManager = new GraphHopperRoadManager("1170b7cf-8fd0-4796-86f5-de3ca31c5d45",false);
                Road road = roadManager.getRoad(waypoints);
                if(road.mStatus != Road.STATUS_OK)
                    Toast.makeText(getApplicationContext(), "Error when loading the road - status="+road.mStatus, Toast.LENGTH_SHORT).show();
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                mMapView.getOverlays().add(roadOverlay);
                mMapView.invalidate();
                return false;
            }
        });


        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapController = (MapController) mMapView.getController();
        GeoPoint gPt = new GeoPoint(49.172167, -0.365908);
        mMapController.setZoom(13);
        mMapController.setCenter(gPt);

    }

    /*
    * Preparing the list data
    */
    private void prepareListData(List<Trip> trips) {


        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Trip>>();

        // Adding child data
        for(Trip trip : trips){
            listDataHeader.add(trip.getDepartureTime()+" --> " + trip.getArrivalTime());
            ArrayList<Trip> tripList = new ArrayList<Trip>();
            tripList.add(trip);
            listDataChild.put(trip.getDepartureTime()+" --> " + trip.getArrivalTime(), tripList);
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

}
