package xyz.anduril.rauros.actionbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Rauros on 7/10/2017.
 */

public class Activity1Map extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    private boolean debug = true;
    private Marker[] generatedMarkers;
    private LatLng wherePlayerIs;

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final String TAG = Activity1Map.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        getPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_player:
                        Intent intentMap = new Intent(Activity1Map.this, MainActivity.class);
                        startActivity(intentMap);
                        break;
                    case R.id.ic_map:
                        updateLocation(mMap);
                        break;
                    case R.id.ic_settings:
                        Intent intentSettings = new Intent(Activity1Map.this, Activity2Settings.class);
                        startActivity(intentSettings);
                        break;
                }
                return false;
            }
        });
        //MATT - putting fancy map stuff in here

    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        styleMap(mMap);
        updateLocation(mMap);
    }
    //MATT - distance calculator
    //thanks https://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
    public double distAway(LatLng A, LatLng B){  // generally used geo measurement function, measuring A to B using HAVERSINE formula
        double lat1 = A.latitude;
        double lon1 = A.longitude;
        double lat2 = B.latitude;
        double lon2 = B.longitude;
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    public LatLng getPlayersLocation() {
        wherePlayerIs = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        return wherePlayerIs;
    }

    public void getPermissions() {
        if (ContextCompat.checkSelfPermission(Activity1Map.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Activity1Map.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(Activity1Map.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void updateLocation(GoogleMap googleMap) {

        mMap = googleMap;

        wherePlayerIs = getPlayersLocation();
        mMap.addMarker(new MarkerOptions().position(wherePlayerIs).title("This is you, $player").icon(BitmapDescriptorFactory.fromResource(R.drawable.youarehere)));
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(wherePlayerIs,17.5f) );
    }

    public void styleMap(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_night_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        //disable annoying toolbar at bottom when clicking markers
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //remove all marker centering on tap
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Marker currentShown;

            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentShown)) {
                    marker.hideInfoWindow();
                    currentShown = null;
                } else {
                    marker.showInfoWindow();
                    currentShown = marker;
                }
                return true;
            }
        });
    }
}
