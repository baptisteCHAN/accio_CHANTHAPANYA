package com.example.damien.test;

import java.util.ArrayList;

/**
 * Created by Damien on 11/01/2017.
 */

public class Trip {
    private String _departure;
    private String _arrival;
    private ArrayList<TripPoint> _points;

    public Trip(){
        _departure = "Départ";
        _arrival = "Arrivée";
        _points = new ArrayList<TripPoint>();
        _points.add(new TripPoint());
    }

    public Trip(String departure, String arrival, ArrayList<TripPoint> tripPoints){
        _departure = departure;
        _arrival = arrival;
        _points = tripPoints;
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
