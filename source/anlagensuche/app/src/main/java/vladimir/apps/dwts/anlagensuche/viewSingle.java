package vladimir.apps.dwts.anlagensuche;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 * WEA Suche
 *
 * @author
 *      Vladimir (jelezarov.vladimir@gmail.com)
 */

public class viewSingle extends FragmentActivity implements OnMapReadyCallback {
    private LatLng goal;
    private String desc = "";
    GoogleMap mMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_view);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Retrieve data from MainActivity on item click event
        Intent i = getIntent();
        String breit = i.getStringExtra("breit");
        String lang = i.getStringExtra("lang");
        desc = i.getStringExtra("desc");
        goal = new LatLng(Double.parseDouble(breit),Double.parseDouble(lang));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(goal).title(desc)).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(goal,10));
    }

}
