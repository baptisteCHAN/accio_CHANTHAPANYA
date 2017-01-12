package com.example.damien.test;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Damien on 11/01/2017.
 */

public class Trip {
    private String _departure;
    private String _arrival;

    public Trip(){
        _departure = "Départ";
        _arrival = "Arrivée";
    }

    public Trip(String departure, String arrival){
        _departure = departure;
        _arrival = arrival;
    }

    public String getDeparture(){
        return _departure;
    }

    public String getArrival(){
        return _arrival;
    }

    public Trip(JSONArray jsonArray){

        try {
            _departure = jsonArray.getJSONObject(0).getString();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
