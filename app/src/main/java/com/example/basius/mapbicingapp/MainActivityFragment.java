package com.example.basius.mapbicingapp;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MapView map;
    BicingApi api = new BicingApi();
    private MyLocationNewOverlay myLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private CompassOverlay mCompassOverlay;
    private IMapController mapController;
    private RadiusMarkerClusterer biciMarkers;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        map = (MapView) view.findViewById(R.id.map);
        initializeMap();
        setZoom();
        setOverlays();
        refresh();
        map.invalidate();

        return view;
    }

    private void setMarkers( ArrayList<Estacio> estacions) {
        setupMarkerOverlay();

        Drawable clusterIconD = getResources().getDrawable(R.drawable.ic_agrupacioestacions);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

        biciMarkers.setIcon(clusterIcon);

        for (Estacio estacio : estacions){
            Marker marker = new Marker(map);
            //Transformem l'string de latitud i longitud en double per obtenir el punt exacte
            GeoPoint point = new GeoPoint(Double.parseDouble(estacio.getLatitude()),Double.parseDouble(estacio.getLongitude()));
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            String infoEstacio = estacio.getStreetName()+", "+ estacio.getStreetNumber()+"\n";
            int ocupacio = (Integer.parseInt(estacio.getBikes())*100/
                    (Integer.parseInt(1+estacio.getSlots())));
            //Diferenciem si es estacio electrica i normal i apliquem icona segons perecentatge
            if(estacio.getType().equals("BIKE")){
                infoEstacio += "MANUAL\n";
                if(ocupacio==0){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_normal_0));
                }else if(ocupacio<=25){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_normal_25));
                }else if(ocupacio<=50){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_normal_50));
                }else if(ocupacio<=75){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_normal_75));
                }else if(ocupacio<=101){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_normal_100));
                }

            }else {
                infoEstacio += "ELECTRICA\n";
                if(ocupacio==0){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_electric_0));
                }else if(ocupacio<=25){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_electric_25));
                }else if(ocupacio<=50){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_electric_50));
                }else if(ocupacio<=75){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_electric_75));
                }else if(ocupacio<=101){
                    marker.setIcon(getResources().getDrawable(R.drawable.ic_electric_100));
                }
            }

            infoEstacio += estacio.getBikes()+" bicis disponibles";
            marker.setTitle(infoEstacio);
            //Calculem el percentatge d'ocupacio


            marker.setAlpha(0.6f);
            biciMarkers.add(marker);
        }
        biciMarkers.invalidate();
        map.invalidate();



    }

    private void setupMarkerOverlay() {

        biciMarkers = new RadiusMarkerClusterer(getContext());
        map.getOverlays().add(biciMarkers);
        biciMarkers.setRadius(100);
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }

    private void setZoom() {
        //  Setteamos el zoom al mismo nivel y ajustamos la posición a un geopunto
        mapController = map.getController();
        mapController.setZoom(15);
    }

    private void setOverlays() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();

        myLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getContext()),
                map
        );
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo( myLocationOverlay
                        .getMyLocation());
            }
        });
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mCompassOverlay = new CompassOverlay(
                getContext(),
                new InternalCompassOrientationProvider(getContext()),
                map
        );
        mCompassOverlay.enableCompass();
        map.getOverlays().add(myLocationOverlay);
        map.getOverlays().add(this.mScaleBarOverlay);
        map.getOverlays().add(this.mCompassOverlay);
    }



    private void refresh(){
        RefreshDataTask task = new RefreshDataTask();
        task.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private class RefreshDataTask extends AsyncTask<Void, Void, ArrayList<Estacio>> {

        @Override
        protected ArrayList<Estacio> doInBackground(Void... voids) {
            ArrayList<Estacio> estacions = api.getStations();
            return estacions;
        }

        @Override
        protected void onPostExecute(ArrayList<Estacio> estacions) {
            setMarkers(estacions);
        }
    }


}
