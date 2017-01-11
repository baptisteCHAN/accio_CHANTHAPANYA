package com.example.damien.test;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class loginActivity extends AppCompatActivity {

    private static final String urlLogin = "http://192.168.12.79";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_page);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
        if(idFile.contains("_id")){
            navigationToHomePage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    public void onLogin(View view){
        EditText loginET = (EditText)findViewById(R.id.login);
        EditText passwordET = (EditText)findViewById(R.id.password);

        if(TextUtils.isEmpty(loginET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Remplissez les champs", Toast.LENGTH_SHORT).show();
            navigationToHomePage();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("login", loginET.getText());
        params.put("password", passwordET.getText());
        invokeWS(params);
    }

    public void  invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlLogin, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Connexion réussie", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonArray = new JSONObject(new String(responseBody));
                    SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
                    SharedPreferences.Editor editor = idFile.edit();
                    editor.putString("_id", jsonArray.get("_id").toString());
                    editor.commit();

                }catch(JSONException e){
                    e.printStackTrace();
                }
                navigationToHomePage();
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
                    Toast.makeText(getApplicationContext(), "  "+error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void navigationToRegisterPage(View view){
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void navigationToHomePage(){
        Intent loginIntent = new Intent(getApplicationContext(),HomeActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }
}
