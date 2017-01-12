package com.example.damien.test;

/**
 * Created by Damien on 12/01/2017.
 */

public class TripPoint{
    private double _longitude;
    private double _lattitude;

    public TripPoint(){
        _longitude = -0.36;
        _lattitude = 49.2;
    }

    public TripPoint(double lattitude, double longitude){
        _longitude = longitude;
        _lattitude = lattitude;
    }

    public double getLongitude(){
        return _longitude;
    }

    public double getLattitude(){
        return _lattitude;
    }
}
