package com.example.damien.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class loginActivity extends AppCompatActivity {

    private static final String urlLogin = "http://192.168.12.79";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_page);
    }

    public void onLogin(View view){
        EditText loginET = (EditText)findViewById(R.id.login);
        EditText passwordET = (EditText)findViewById(R.id.password);

        if(TextUtils.isEmpty(loginET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Remplissez les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("login", loginET.getText());
        params.put("password", passwordET.getText());
        invokeWS(params);
    }

    public void  invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlLogin, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Enregistrement réussi", Toast.LENGTH_SHORT).show();

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
