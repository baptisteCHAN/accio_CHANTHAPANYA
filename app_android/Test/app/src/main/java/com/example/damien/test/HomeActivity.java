package com.example.damien.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.views.overlay.Marker;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<TripPoint> trip;
    private ProgressDialog prgDialog;
    private List<Trip> trips;
    private static final String urlGetTrajet = "http://192.168.12.79:3000/users";
    Intent iReservationView;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        iReservationView = new Intent(this, ReservationView.class);
        extras = iReservationView.getExtras();
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Connexion en cours ... ");
        prgDialog.setCancelable(false);
    }

    public void navigationToMyReservationsPage(){
        Intent homeIntent = new Intent(getApplicationContext(),ReservationView.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

    public void navigationToReservationPage(View view){
        Intent homeIntent = new Intent(getApplicationContext(),ReservationActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void navigationToLoginPage(View view){
        SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
        SharedPreferences.Editor editor = idFile.edit();
        editor.remove("_id");
        editor.commit();
        Intent homeIntent = new Intent(getApplicationContext(),loginActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void onReservationView(View view){
        prgDialog.show();
        RequestParams params = new RequestParams();
        invokeWS(params);
    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlGetTrajet, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                Toast.makeText(getApplicationContext(),"connexion réussie", Toast.LENGTH_SHORT).show();
                try {

                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
                    String id = idFile.getString("_id","");
                    SharedPreferences.Editor editor = idFile.edit();
                    for(int i = 0;i<jsonArray.length();i++) {
                        if (jsonArray.getJSONObject(i).getString("_id").equals(id)) {
                            String tmp = jsonArray.getJSONObject(i).getJSONObject("trajet").toString();
                            editor.putString("jsonPOINTS",tmp);
                            editor.commit();
                            navigationToMyReservationsPage();
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error ) {
                prgDialog.hide();
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
