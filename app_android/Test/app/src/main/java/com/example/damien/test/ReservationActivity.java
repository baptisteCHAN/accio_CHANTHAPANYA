package com.example.damien.test;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ReservationActivity extends Activity {

    private static final String urlReservation = "http://192.168.12.79:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_page);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_arrival);
        timePicker.setIs24HourView(true);
    }

    public void onReservation(View view){
        EditText arrivalAddressET = (EditText)findViewById(R.id.arrivalAddress);
        EditText departureAddressET = (EditText)findViewById(R.id.departureAddress);


        if(TextUtils.isEmpty(departureAddressET.getText().toString().trim()) || TextUtils.isEmpty(arrivalAddressET.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressDeparture = geocoder.getFromLocationName(departureAddressET.getText().toString(), 1);
            List<Address> addressArrival = geocoder.getFromLocationName(arrivalAddressET.getText().toString(), 1);

            if(addressDeparture.isEmpty()){
                Toast.makeText(getApplicationContext(), "adresse de départ inconnue", Toast.LENGTH_SHORT).show();
                return;
            }
            if(addressArrival.isEmpty()){
                Toast.makeText(getApplicationContext(), "adresse d'arrivée inconnue", Toast.LENGTH_SHORT).show();
                return;
            }
            RequestParams params = new RequestParams();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("lat", addressDeparture.get(0).getLatitude());
            jsonParams.put("lon", addressDeparture.get(0).getLongitude());
            jsonParams.put("lat", addressArrival.get(0).getLatitude());
            jsonParams.put("lon", addressArrival.get(0).getLongitude());
            //invokeWS(params);


        }catch(IOException e){
            e.printStackTrace();
        }catch(JSONException e){
            e.printStackTrace();
        }




    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlReservation, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Réservation réussie", Toast.LENGTH_SHORT).show();
                navigationToReservationView();
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
    }

    public void navigationToReservationView(){
        Intent RegisterIntent = new Intent(getApplicationContext(),loginActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(RegisterIntent);
    }

    public void checkboxClicked(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.go_now_checkbox);
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_arrival);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_arrival);
        if (checkBox.isChecked()){
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
        }else{
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.VISIBLE);
        }
    }
}

