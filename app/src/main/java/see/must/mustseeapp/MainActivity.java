package see.must.mustseeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import java.util.List;

import see.must.mustseeapp.Model.InterestPoint;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SHOW_ABOUTUSACTIVITY = 3;
    private static final int SHOW_NEWPOINTACTIVITY = 4;
    private static final int SHOW_SHOWINTERESPOINTACTIVITY = 5;
    private static final int SHOW_SEARCH = 6;
    private MapView mapView;
    ArrayAdapter<InterestPoint> todoItemsAdapter;
    private MapboxMap mapboxMap = null;
    InterestPoint aInterestPoint;
    Bundle bundle = new Bundle();

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
                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitud", point.getLatitude());
                        bundle.putDouble("longitud", point.getLongitude());
                        Intent intent = new Intent(getApplicationContext(), NewInteresPointActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, SHOW_NEWPOINTACTIVITY);
                    }
                });

                mapboxMap.getMarkerViewManager().setOnMarkerViewClickListener(new MapboxMap.OnMarkerViewClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {
                        Log.v("Datos punto:" , marker.getPosition().toString());
                        Timber.e(marker.toString());


                        bundle.putDouble("latitud", marker.getPosition().getLatitude());
                        bundle.putDouble("longitud", marker.getPosition().getLongitude());
                        bundle.putString("name", marker.getTitle().toString());

                        getInterestPointServer(marker.getTitle().toString(),marker.getPosition().getLatitude(),marker.getPosition().getLongitude(),1);

                        return false;
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
                //BUSCADOR AQUÍ QUE LLAMA A LAYOUT PARA LISTADO
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent, SHOW_SEARCH);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.historial) {

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

    public void getInterestPointServer(String name, Double latitud, Double longitud, final int n) {
        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.whereEqualTo("nombre", name);
        query.whereEqualTo("latitud", latitud);
        query.whereEqualTo("longitud", longitud);
        query.findInBackground(new FindCallback<InterestPoint>() {
            public void done(List<InterestPoint> objects, ParseException e) {
                if (e == null) {
                    todoItemsAdapter = new ArrayAdapter<InterestPoint>(getApplicationContext(), R.layout.content_main, R.id.mapView, objects);
                    if (todoItemsAdapter.getCount() == 1) {
                        aInterestPoint = todoItemsAdapter.getItem(0);
                        aInterestPoint.descripcion = aInterestPoint.getDescripcion();
                        bundle.putString("descripcion", aInterestPoint.descripcion);
                        //imagen

                        Intent intent = new Intent(getApplicationContext(), ShowInteresPointActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, SHOW_SHOWINTERESPOINTACTIVITY);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SHOW_NEWPOINTACTIVITY) {
                Bundle bundle = data.getExtras();
                Double latitud = bundle.getDouble("latitud");
                Double longitud = bundle.getDouble("longitud");
                String name = bundle.getString("name");
                String description = bundle.getString("description");
                newParseObject(name, description, latitud, longitud);
            }
            else{
                if (requestCode == SHOW_SHOWINTERESPOINTACTIVITY){
                    Bundle bundle = data.getExtras();
                    meterAHistorial(bundle);
                }
                else{
                    if(requestCode == SHOW_SEARCH){
                        Bundle bundle = data.getExtras();
                        String nombre = bundle.getString("name");
                        getInterestPointServer(nombre,-1.,-1.,0);
                    }
                }
            }
        }
    }
    private void newParseObject(String name, String description, Double latitud, Double longitud) {

        aInterestPoint = new InterestPoint();
        aInterestPoint.setNombre(name);
        aInterestPoint.setLatitud(latitud);
        aInterestPoint.setLongitud(longitud);
        aInterestPoint.setDescription(description);

        aInterestPoint.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    todoItemsAdapter.notifyDataSetChanged();
                    Log.v("object saved in server:", "newParseObject()");
                } else {
                    Log.v("save failed, reason: "+ e.getMessage(), "newParseObject()");
                    Toast.makeText(
                            getBaseContext(),
                            "newParseObject(): Object save failed  to server, reason: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
        this.getServerList();
    }

    public void meterAHistorial(Bundle bundle){}
}




