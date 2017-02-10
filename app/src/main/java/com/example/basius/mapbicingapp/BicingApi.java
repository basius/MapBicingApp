package com.example.basius.mapbicingapp;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by basius on 7/02/17.
 */

public class BicingApi {
    public static final String BASE_URL = "http://wservice.viabicing.cat/v2/stations";

    ArrayList<Estacio> getStations(){
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .build();
        String url = builtUri.toString();
        return doCall(url);
    }
    @Nullable
    ArrayList<Estacio> doCall(String url) {
        try {
            String JsonResponse = HttpUtils.get(url);
            return processJson(JsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    ArrayList<Estacio> processJson(String jsonResponse) {
        ArrayList<Estacio> estacions = new ArrayList<Estacio>();
        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONArray stations = data.getJSONArray("stations");
                for(int station = 0; station < stations.length(); station++){
                    JSONObject estacioActual = stations.getJSONObject(station);
                    Estacio e = new Estacio();
                    e.setId(estacioActual.getString("id"));
                    e.setType(estacioActual.getString("type"));
                    e.setLatitude(estacioActual.getString("latitude"));
                    e.setLongitude(estacioActual.getString("longitude"));
                    e.setStreetName(estacioActual.getString("streetName"));
                    e.setStreetNumber(estacioActual.getString("streetNumber"));
                    e.setAltitude(estacioActual.getString("altitude"));
                    e.setSlots(estacioActual.getString("slots"));
                    e.setBikes(estacioActual.getString("bikes"));
                    e.setNearbyStations(estacioActual.getString("nearbyStations"));
                    e.setStatus(estacioActual.getString("status"));
                    estacions.add(e);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ESTACIONS:",estacions.toString());
        return estacions;
    }
}
