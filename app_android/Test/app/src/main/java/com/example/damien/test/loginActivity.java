package com.example.damien.test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class loginActivity extends AppCompatActivity {

    private static final String urlLogin = "http://192.168.12.79:3000/users";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private ProgressDialog prgDialog;


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
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Connexion en cours ... ");
        prgDialog.setCancelable(false);
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
        prgDialog.show();
        EditText loginET = (EditText)findViewById(R.id.login);
        EditText passwordET = (EditText)findViewById(R.id.password);

        if(TextUtils.isEmpty(loginET.getText().toString().trim()) || TextUtils.isEmpty(passwordET.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Remplissez les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        invokeWS(params, loginET.getText().toString(), passwordET.getText().toString());
    }

    public void  invokeWS(RequestParams params, final String login, final String password){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlLogin, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonTMP = jsonArray.getJSONObject(i);
                        if(jsonTMP.getString("username").equals(login) && jsonTMP.getString("passwordSalt").equals(password)){
                            SharedPreferences idFile = getSharedPreferences(getString(R.string.idFile), MODE_PRIVATE);
                            SharedPreferences.Editor editor = idFile.edit();
                            editor.putString("_id", jsonTMP.getString("_id"));
                            editor.commit();
                            Toast.makeText(getApplicationContext(),"Connexion réussie", Toast.LENGTH_SHORT).show();
                            navigationToHomePage();
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Echec de la connexion", Toast.LENGTH_SHORT).show();
                }catch(JSONException e){
                    e.printStackTrace();
                    prgDialog.hide();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error ) {
                prgDialog.hide();
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Error 404, Requested resource not found", Toast.LENGTH_LONG).show();
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
