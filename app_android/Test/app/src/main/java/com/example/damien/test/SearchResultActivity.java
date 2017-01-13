package com.example.damien.test;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
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
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Damien on 11/01/2017.
 */

public class SearchResultActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Trip>> listDataChild;
    private static final String urlGetTrajet = "http://192.168.12.79:3000/centrectrl/";
    private static String urlReservation = "http://192.168.12.79:3000/centrectrl/demande";
    private Trip currentTrip = null;
    private MapView mMapView;
    private MapController mMapController;
    private List<Trip> trips;
    private ArrayList<TripPoint> trip;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.search_result);

        expListView = (ExpandableListView) findViewById(R.id.expandList);

        RequestParams params = new RequestParams();
        invokeWS(params);
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

                Button btn = (Button) findViewById(R.id.button_reserver);
                btn.setEnabled(true);

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

        Button confirmbtn = (Button) findViewById(R.id.button_reserver);
        confirmbtn.setEnabled(false);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SearchResultActivity.this);
                alert.setTitle("Réservation!");
                alert.setMessage("Etes-vous sûr de vouloir réserver ce trajet?");
                alert.setPositiveButton("Oui, réserver maintenant!", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        //Lancer la résa
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("Non", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

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

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlGetTrajet, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Enregistrement réussi", Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for(int i=0;i<jsonArray.length();i++){
                        trip = new ArrayList<TripPoint>();
                        JSONObject jsonTMP = jsonArray.getJSONObject(i);
                        trip.add(new TripPoint(jsonTMP.getDouble("lat"),jsonTMP.getDouble("lon")));
                        trip.add(new TripPoint(jsonTMP.getDouble("lat"),jsonTMP.getDouble("lon")));
                        trips.add(new Trip(jsonTMP.getString("departureAddress"), jsonTMP.getString("arrivalAddress"), trip, jsonTMP.getString("departureHour"), jsonTMP.getString("arrivalHour")));
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

    public void onReservation(View view){

        try {
            JSONObject jsonParams = new JSONObject();
            JSONObject departureJSON = new JSONObject();

            ArrayList<TripPoint> tripPoints = currentTrip.getPoints();
            departureJSON.put("lon", tripPoints.get(0).getLongitude());
            departureJSON.put("lat", tripPoints.get(0).getLattitude());
            jsonParams.put("depart", departureJSON);
            JSONObject arrivalJSON = new JSONObject();
            arrivalJSON.put("lon",tripPoints.get(tripPoints.size()-1).getLongitude());
            arrivalJSON.put("lat", tripPoints.get(tripPoints.size()-1).getLattitude());
            jsonParams.put("arrivee", arrivalJSON);
            SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
            if(idFile.contains("_id")){
                jsonParams.put("userID",idFile.getString("_id",""));
            }
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("newIte",jsonParams);
            invokeWSJSON(jsonParam);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void invokeWSJSON(JSONObject jsonParams){
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            StringEntity entity = new StringEntity(jsonParams.toString());


            client.post(getApplicationContext(), urlReservation, entity, "application/json",new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(),"Réservation réussie", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! "+statusCode + "  "+error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
