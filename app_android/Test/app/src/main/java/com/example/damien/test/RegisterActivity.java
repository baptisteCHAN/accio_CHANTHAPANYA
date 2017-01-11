package com.example.damien.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;


public class RegisterActivity extends AppCompatActivity {

    private static final String urlRegister = "http://192.168.12.79:3000/user";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
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

    public void onSubscribe(View view){
        EditText nameET = (EditText) findViewById(R.id.name);
        EditText firstNameET = (EditText)findViewById(R.id.first_name);
        EditText birthDateET = (EditText)findViewById(R.id.date_of_birth);
        EditText loginET = (EditText)findViewById(R.id.login);
        EditText passwordET = (EditText)findViewById(R.id.password);
        EditText confirmPasswordET = (EditText)findViewById(R.id.confirm_pass);

        if(TextUtils.isEmpty(loginET.getText().toString().trim()) || TextUtils.isEmpty(nameET.getText().toString().trim()) || TextUtils.isEmpty(firstNameET.getText().toString().trim()) || TextUtils.isEmpty(confirmPasswordET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!passwordET.getText().toString().equals(confirmPasswordET.getText().toString())){
            Toast.makeText(getApplicationContext(), "Different password", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("name", nameET.getText());
        params.put("firstName", firstNameET.getText());
        params.put("birthday", birthDateET.getText());
        params.put("login", loginET.getText());
        params.put("password", passwordET.getText());
        invokeWS(params);
    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlRegister, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"Enregistrement réussi", Toast.LENGTH_SHORT).show();
                navigationToHostPage();
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

    public void navigationToHostPage(){
        Intent RegisterIntent = new Intent(getApplicationContext(),loginActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(RegisterIntent);
    }
}
