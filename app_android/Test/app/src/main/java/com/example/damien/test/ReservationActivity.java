package com.example.damien.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ReservationActivity extends Activity {

    private static final String urlReservation = "http://192.168.12.79:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

    }

    public void onReservation(View view){
        EditText numeroArrival = (EditText)findViewById(R.id.numeroArrival);
        EditText numeroDeparture = (EditText)findViewById(R.id.numeroDeparture);
        EditText streetArrival = (EditText)findViewById(R.id.streetArrival);
        EditText streetDeparture = (EditText)findViewById(R.id.streetDeparture);
        EditText cityArrival = (EditText)findViewById(R.id.cityArrival);
        EditText cityDeparture = (EditText)findViewById(R.id.cityDeparture);

        if(TextUtils.isEmpty(numeroArrival.getText().toString().trim()) || TextUtils.isEmpty(numeroDeparture.getText().toString().trim()) || TextUtils.isEmpty(streetArrival.getText().toString().trim()) ||
                TextUtils.isEmpty(streetDeparture.getText().toString().trim()) || TextUtils.isEmpty(cityArrival.getText().toString().trim()) || TextUtils.isEmpty(cityDeparture.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("numeroArrival",numeroArrival);
        params.put("numeroDeparture",numeroDeparture);
        params.put("streetArrival", streetArrival);
        params.put("streetDeparture", streetDeparture);
        params.put("cityArrival", cityArrival);
        params.put("cityDeparture", cityDeparture);
        invokeWS(params);


    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlReservation, params, new AsyncHttpResponseHandler() {
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
    }
}

