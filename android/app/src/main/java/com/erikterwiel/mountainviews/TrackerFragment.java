package com.erikterwiel.mountainviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class TrackerFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "TrackerFragment.java";

    MapView gMapView;
    GoogleMap gMap = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        MapsInitializer.initialize(getActivity());
        gMapView = (MapView) view.findViewById(R.id.tracker_map);
        gMapView.getMapAsync(this);
        gMapView.onCreate(getArguments());
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.951883, -75.191222), 20));
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gMapView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        gMapView.onResume();
        if (gMapView != null)
            gMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gMapView.onDestroy();
        if (gMapView != null)
            gMapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (gMapView != null)
            gMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (gMapView != null)
            gMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        gMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (gMapView != null)
            gMapView.onSaveInstanceState(outState);
    }
}
