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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class RegisterActivity extends AppCompatActivity {

    private static final String urlRegister = "http://192.168.12.79:3000/users/create";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

    }



    public void onSubscribe(View view){
        EditText emailET = (EditText) findViewById(R.id.email);
        EditText loginET = (EditText)findViewById(R.id.login);
        EditText passwordET = (EditText)findViewById(R.id.password);
        EditText confirmPasswordET = (EditText)findViewById(R.id.confirm_pass);

        if(TextUtils.isEmpty(loginET.getText().toString().trim()) || TextUtils.isEmpty(emailET.getText().toString().trim()) || TextUtils.isEmpty(confirmPasswordET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!passwordET.getText().toString().equals(confirmPasswordET.getText().toString())){
            Toast.makeText(getApplicationContext(), "Different password", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email", emailET.getText());
            jsonParams.put("username", loginET.getText());
            jsonParams.put("passwordSalt", passwordET.getText());
            jsonParams.put("role", "0");
            invokeWS(jsonParams);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void invokeWS(JSONObject jsonParams){
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            StringEntity entity = new StringEntity(jsonParams.toString());

        client.post(getApplicationContext(), urlRegister, entity, "application/json", new AsyncHttpResponseHandler() {
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
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    public void navigationToHostPage(){
        Intent RegisterIntent = new Intent(getApplicationContext(),loginActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(RegisterIntent);
    }
}
