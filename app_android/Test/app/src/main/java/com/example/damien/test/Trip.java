package com.example.damien.test;

import org.json.JSONArray;

import org.json.JSONException;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Damien on 11/01/2017.
 */

public class Trip {
    private String _departure;
    private String _arrival;
    private ArrayList<TripPoint> _points;
    private String _departureTime;
    private String _arrivalTime;

    public Trip(){
        _departure = "Départ";
        _arrival = "Arrivée";
        _points = new ArrayList<TripPoint>();
        _points.add(new TripPoint());
        _departureTime = "00:00";
        _arrivalTime = "01:00";
    }

    public Trip(String departure, String arrival, ArrayList<TripPoint> tripPoints){
        _departure = departure;
        _arrival = arrival;
        _points = tripPoints;
        _departureTime = "00:00";
        _arrivalTime = "01:00";
    }

    public Trip(String departure, String arrival, ArrayList<TripPoint> tripPoints, String departureTime, String arrivalTime){
        _departure = departure;
        _arrival = arrival;
        _points = tripPoints;
        _departureTime = departureTime;
        _arrivalTime = arrivalTime;
    }

    public String getDepartureTime(){
        return _departureTime;
    }

    public String getArrivalTime(){
        return _arrivalTime;
    }

    public String getDeparture(){
        return _departure;
    }

    public String getArrival(){
        return _arrival;
    }


    public String toString(){
        return _departure + " --> " + _arrival;
    }

    public ArrayList<TripPoint> getPoints(){
        return _points;
    }
}
