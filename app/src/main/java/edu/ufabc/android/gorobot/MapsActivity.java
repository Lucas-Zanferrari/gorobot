package edu.ufabc.android.gorobot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPS_ACTIVITY";
    private GoogleMap mMap;
    private LatLng currentLatLng;
    private Marker marker;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LAT = "latKey";
    public static final String LNG = "lngKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near UFABC.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int inputCount = sharedpreferences.getInt("inputCount", 0);
        LatLng latLng;

        if(inputCount == 0){
            // Add a marker in ufabc and move the camera
            latLng = new LatLng(-23.643718, -46.527315);
        }
        else {
            latLng = new LatLng(Double.valueOf(sharedpreferences.getString(LAT + (inputCount-1), null)),
                    Double.valueOf(sharedpreferences.getString(LNG + (inputCount-1), null)));
        }

        changeMarker(latLng);

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                changeMarker(latLng);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                changeMarker(latLng);
            }
        });
    }

    private void changeMarker(LatLng latLng){
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()).draggable(true));
        // Move the camera instantly to location with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        currentLatLng = latLng;
    }

    public void sendLatLng(View view){
        if(marker != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LATITUDE", currentLatLng.latitude);
            intent.putExtra("LONGITUDE", currentLatLng.longitude);

            //saveInSharedPreferences(String.valueOf(currentLatLng.latitude), String.valueOf(currentLatLng.longitude));

            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(MapsActivity.this, "Selecione um local.", Toast.LENGTH_LONG).show();
        }
    }

    /*
    public void saveInSharedPreferences(String lat, String lng){
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedpreferences.edit();
        int inputCount = sharedpreferences.getInt("inputCount", 0);

        sharedPrefsEditor.putString(LAT+inputCount, lat);
        sharedPrefsEditor.putString(LNG+inputCount, lng);
        inputCount++;
        sharedPrefsEditor.putInt("inputCount", inputCount);
        sharedPrefsEditor.commit();
    }
    */
}
