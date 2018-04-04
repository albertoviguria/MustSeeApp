package see.must.mustseeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.models.Position;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import see.must.mustseeapp.Model.InterestPoint;
import see.must.mustseeapp.Model.TravelPointsApplication;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SHOW_ABOUTUSACTIVITY = 3;
    private static final int SHOW_NEWPOINTACTIVITY = 4;
    private MapView mapView;
    ArrayAdapter<InterestPoint> todoItemsAdapter;
    private MapboxMap mapboxMap = null;
    TravelPointsApplication tpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoiaW5pZ29sZXJnYSIsImEiOiJjamRlZWRjemswYmx4MzNwYzE4YWc2czg3In0.R6vOf25m3XlOTz-lmrTZ8g");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        try {
            ParseObject.registerSubclass(InterestPoint.class);

            Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                    .applicationId("myAppId")
                    .clientKey("empty")
                    .server("https://mustseeapp.herokuapp.com/parse/")
                    .build());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap map) {
                mapboxMap = map;

                getServerList();

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        String string = String.format(Locale.US, "User clicked at: %s", point.toString());
                        Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();

                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitud", point.getLatitude());
                        bundle.putDouble("longitud", point.getLongitude());
                        Intent intent = new Intent(getApplicationContext(), NewInteresPointActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, SHOW_NEWPOINTACTIVITY);
                    }
                });
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.buscar) {
            // Handle the camera action
        } else if (id == R.id.historial) {

        } else if (id == R.id.aboutUs) {

            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivityForResult(intent, SHOW_ABOUTUSACTIVITY);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void getServerList() {
        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.findInBackground(new FindCallback<InterestPoint>() {
            public void done(List<InterestPoint> objects, ParseException e) {
                if (e == null) {
                    todoItemsAdapter = new ArrayAdapter<InterestPoint>(getApplicationContext(), R.layout.content_main, R.id.mapView, objects);

                    for (int i = 0; i <= todoItemsAdapter.getCount() - 1; i = i + 1) {
                        InterestPoint punto = todoItemsAdapter.getItem(i);

                        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                        Icon icon = iconFactory.fromResource(R.drawable.marker);

                        mapboxMap.addMarker(new MarkerViewOptions()
                                .position(new LatLng(punto.getLatitud(), punto.getLongitud()))
                                .icon(icon)
                                .title(punto.getNombre()));
                    }
                } else {
                    Log.v("error query, reason: " + e.getMessage(), "getServerList()");
                    Toast.makeText(
                            getBaseContext(),
                            "getServerList(): error  query, reason: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




