package com.example.basius.mapbicingapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MapView map;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        map = (MapView) view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(48.13, -1.63);
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);
        map.invalidate();
        return view;
    }
}
