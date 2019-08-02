package com.dewcis.baraza;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TestActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment smf;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location current, start, stop;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        context = this;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        @SuppressLint("MissingPermission") Task<Location> location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    current = (Location) task.getResult();
                    smf.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng place = current!=null ? new LatLng(current.getLatitude(),current.getLongitude()):new LatLng(-33.852, 151.211);
                            googleMap.addMarker(new MarkerOptions().position(place)
                                    .title("Marker in Sydney"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                            googleMap.setMyLocationEnabled(true);
                        }
                    });
                }
                else{
                    Toast.makeText(context,"couldnt get location",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng place = current!=null ? new LatLng(current.getLatitude(),current.getLatitude()):new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(place)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));

    }
}
