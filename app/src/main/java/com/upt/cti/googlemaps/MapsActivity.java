package com.upt.cti.googlemaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQ_PERMISSION = 1;
    private GoogleMap mMap;
    private LatLng facultate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        else
            askPermission();

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        facultate = new LatLng(45.7450284, 21.2275766);

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        iconGenerator.setTextAppearance(R.color.textColor);
        mMap.addMarker(new MarkerOptions().position(facultate).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Facultate"))));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(facultate));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        List<LatLng> list = new ArrayList<LatLng>();
        list.add(sydney);
        list.add(facultate);

        drawPolyLineOnMap(list, mMap);

        onMarkerClick();

        Log.d("AS12", String.valueOf(SphericalUtil.computeDistanceBetween(facultate, sydney)));
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQ_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPermission())
                        mMap.setMyLocationEnabled(true);
                }else{

                }
                break;
        }
    }

    public void drawPolyLineOnMap(List<LatLng> list, GoogleMap googleMap){
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.GREEN);
        polyOptions.width(8);
        polyOptions.addAll(list);
        googleMap.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latLng : list){
            builder.include(latLng);
        }
        builder.build();
    }

    public void onMarkerClick(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                    if(marker.getPosition().equals(facultate)){
                        Toast.makeText(MapsActivity.this, "I'm studying", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }
}