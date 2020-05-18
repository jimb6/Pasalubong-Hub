package com.allandroidprojects.ecomsample.ui.composer.user.merchant.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.composer.user.merchant.MerchantProfileActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    MapView mMapView;
    private GoogleMap googleMap;

    private int REQUEST_CODE_READ_STORAGE = 1;

    private void addMapMarker() {
        LatLng lcocation = new LatLng(Double.parseDouble(MerchantProfileActivity.business.getLat()),
                Double.parseDouble(MerchantProfileActivity.business.getLng()));
        googleMap.addMarker(new MarkerOptions().position(lcocation)
                .title(MerchantProfileActivity.business.getBusinessName()));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lcocation));
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lcocation).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_READ_STORAGE);
        }
    }

    private void addPinPoint() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        askForPermission();
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getContext(), "Location disabled", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        double lat = Double.parseDouble(MerchantProfileActivity.business.getLat());
        double lng = Double.parseDouble(MerchantProfileActivity.business.getLng());

        Polyline polyline1 = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(location.getLatitude(), location.getLongitude()),
                        new LatLng(lat, lng)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // For showing a move to my location button
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnPolylineClickListener(this);
        this.googleMap.setOnPolygonClickListener(this);
        addMapMarker();
    }

    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }
}
