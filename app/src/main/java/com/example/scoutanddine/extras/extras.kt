package com.example.scoutanddine.extras

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng

fun calculateDistance(lat1: Double, lng1: Double, lat2: Location?): Float {
    lat2?.let {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lng1, it.latitude, it.longitude, results)
        Log.d("RADIUS:", results[0].toString())
        return results[0] / 1000 // Distance in km

    }

    return Float.MIN_VALUE
}