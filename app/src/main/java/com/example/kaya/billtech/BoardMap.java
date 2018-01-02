package com.example.kaya.billtech;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;
    ConnectionClass connectionClass;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> locations = new ArrayList<>();
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LatLng latlng0;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //startService(new Intent(this, LocationService.class));


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 250, 250, 0);

        mapFragment.getMapAsync(this);
        connectionClass = new ConnectionClass();
        new FetchDatasToMap().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //map = new HashMap<String, String>();
        //map.put("LocationID", "0");
        //map.put("Latitude", Double.toString(location.getLatitude()));
        double lat0 = location.getLatitude();
        double long0 = location.getLongitude();
        latlng0 = new LatLng(lat0, long0);
        //map.put("Longitude", Double.toString(location.getLongitude()));
        //map.put("LocationName", "Current Position");
        //locations.add(map);
        if (i == 1) {
            mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng0));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng0, 12));
            i++;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_profile) {
            Intent profile = new Intent(BoardMap.this, ProfilActivity.class);
            startActivity(profile);
        } else if (id == R.id.nav_billboard) {
            Intent billboard = new Intent(BoardMap.this, MyAdverts.class);
            startActivity(billboard);

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(BoardMap.this, SettingsActivity.class);
            startActivity(settings);

        } else if (id == R.id.nav_main) {
            Intent s = new Intent(BoardMap.this, BoardMap.class);
            startActivity(s);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class FetchDatasToMap extends AsyncTask<Object, Object, ArrayList<HashMap<String, String>>> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Object... params) {
            try {
                Connection con = connectionClass.connectionDb();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    String query;
                    query = "select * from BillboardInfo";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        map = new HashMap<String, String>();
                        map.put("LocationID", String.valueOf(rs.getString("billboard_id")));
                        map.put("Latitude", String.valueOf(rs.getString("billboard_locationx")));
                        map.put("Longitude", String.valueOf(rs.getString("billboard_locationy")));
                        map.put("LocationName", String.valueOf(rs.getString("billboard_name")));
                        locations.add(map);
                    }
                    message = "Added Successfully";
                    isSuccess = true;
                    con.close();
                    return locations;
                }
            } catch (Exception ex) {

                isSuccess = false;
                ex.printStackTrace();
                message = "Exceptions";
            }
            return null;
        }

        protected void onPostExecute(ArrayList<HashMap<String, String>> unused) { //Posttan sonra
            for (int i = 0; i < locations.size(); i++) {
                Latitude = Double.parseDouble(locations.get(i).get("Latitude"));
                Longitude = Double.parseDouble(locations.get(i).get("Longitude"));
                String name = locations.get(i).get("LocationName");
                //MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(name);
                //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //mMap.addMarker(marker);
                LatLng latLng = new LatLng(Latitude, Longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));

            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerTitle = marker.getTitle();
                    float[] dist = new float[1];
                    for (int i = 0; i < locations.size(); i++) {
                        String locationTitle = markerTitle;
                        if (locationTitle.equalsIgnoreCase(locations.get(i).get("LocationName"))) {
                            double xlocation = Double.parseDouble(locations.get(i).get("Latitude"));
                            double ylocation = Double.parseDouble(locations.get(i).get("Longitude"));
                            double curlocationx = latlng0.latitude;
                            double curlocationy = latlng0.longitude;
                            Location.distanceBetween(curlocationx, curlocationy, xlocation, ylocation, dist);
                            if (dist[0] / 1000 > 1) {
                                Toast.makeText(getApplicationContext(), "You are not close to this billboard", Toast.LENGTH_LONG).show();
                            } else {
                                Intent intent = new Intent(BoardMap.this, Board.class);
                                intent.putExtra("billBoard_name", markerTitle);
                                startActivity(intent);
                                return false;
                            }
                        }
                    }
                    return false;
                }
            });

        }
    }

}
