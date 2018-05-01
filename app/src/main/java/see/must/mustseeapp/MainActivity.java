package see.must.mustseeapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEngineProvider;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import see.must.mustseeapp.Model.InterestPoint;
import timber.log.Timber;

import static java.lang.Math.abs;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SHOW_ABOUTUSACTIVITY = 3;
    private static final int SHOW_NEWPOINTACTIVITY = 4;
    private static final int SHOW_SHOWINTERESPOINTACTIVITY = 5;

    private static final int SHOW_SEARCH = 6;
    private static final int SHOW_HISTORIALSACTIVITY = 7;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    private MapView mapView;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
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
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();

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
                checkLocationPermission();

                getServerList();

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitud", point.getLatitude());
                    bundle.putDouble("longitud", point.getLongitude());
                    Intent intent = new Intent(getApplicationContext(), NewInterestPointActivity.class);
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
                mapboxMap.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(Marker marker) {
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
                checkLocationPermission();

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
        if (id == R.id.search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, SHOW_SEARCH);
        }
        else if (id == R.id.historial) {
            Intent intent = new Intent(this, ShowHistorialActivity.class);
            startActivityForResult(intent, SHOW_HISTORIALSACTIVITY);
        }
        else if (id == R.id.aboutUs) {
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
                final IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                for (int i = 0; i <= todoItemsAdapter.getCount() - 1; i = i + 1) {
                    final InterestPoint punto = todoItemsAdapter.getItem(i);
                    Log.v("punto interes:", punto.getObjectId());

                    ParseFile iconImage = (ParseFile)punto.get("icon");

                    iconImage.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap iconBm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Bitmap overlayBm = BitmapFactory.decodeResource(getResources(), R.drawable.icon_overlay);
                            Log.v("image query: ", "got image from server");
                            Bitmap totalBm = Bitmap.createBitmap(overlayBm.getWidth(), overlayBm.getHeight(), overlayBm.getConfig());
                            Canvas canvas = new Canvas(totalBm);
                            canvas.drawBitmap(iconBm, new Matrix(), null);
                            canvas.drawBitmap(overlayBm, 0, 0, null);
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(punto.getLatitud(), punto.getLongitud()))
                                    .icon(iconFactory.fromBitmap(totalBm))
                                    .title(punto.getNombre())
                            );
                        } else {
                            // something went wrong
                        }
                        }
                    });
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
                        bundle.putString("id", aInterestPoint.getObjectId());

                        Intent intent = new Intent(getApplicationContext(), ShowInterestPointActivity.class);
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
                try {
                    Bundle bundle = data.getExtras();
                    Double latitud = bundle.getDouble("latitud");
                    Double longitud = bundle.getDouble("longitud");
                    String name = bundle.getString("name");
                    String description = bundle.getString("description");

                    if(name.isEmpty() | description.isEmpty()){
                        Toast.makeText(getBaseContext(), "Algún campo no ha sido rellenado correctamente!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String filePath = bundle.getString("imagePath");
                        Bitmap bitmapToUpload = BitmapFactory.decodeFile(filePath);
                        Bitmap iconBitmapToUpload = Bitmap.createScaledBitmap(bitmapToUpload, 150, 150, true);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                        // Compress image to lower quality scale 1 - 100
                        bitmapToUpload.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        iconBitmapToUpload.compress(Bitmap.CompressFormat.PNG, 100, iconStream);

                        byte[] imageFileData = stream.toByteArray();
                        byte[] iconFileData = iconStream.toByteArray();

                        ParseFile image = new ParseFile("original.jpg", imageFileData);
                        ParseFile icon = new ParseFile("icon.png", iconFileData);
                        image.saveInBackground();
                        icon.saveInBackground();

                        newParseObject(name, description, latitud, longitud, image, icon);
                    }
                }
                catch(Exception e){
                    Toast.makeText(getBaseContext(), "Algún campo no ha sido rellenado correctamente!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(requestCode == SHOW_SEARCH){
                }
            }
        }
    }
    private void newParseObject(final String name, final String description, final Double latitud, final Double longitud, final ParseFile image, final ParseFile icon) {
        ParseQuery<InterestPoint> query = ParseQuery.getQuery("InterestPoint");
        query.whereEqualTo("nombre", name);
        query.findInBackground(new FindCallback<InterestPoint>() {
            public void done(List<InterestPoint> objects, ParseException e) {
            if (e == null) {
                Boolean guardar = true;
                todoItemsAdapter = new ArrayAdapter<InterestPoint>(getApplicationContext(), R.layout.content_main, R.id.mapView, objects);
                if(todoItemsAdapter.getCount() != 0 ) {
                    for (int i = 0; i < todoItemsAdapter.getCount(); i = i + 1) {
                        InterestPoint aux = todoItemsAdapter.getItem(i);
                        Double distancia = abs(aux.getLatitud() - latitud) + abs((aux.getLongitud() - longitud));
                        if (distancia < 0.05) {
                            guardar = false;
                        }
                    }
                }
                if (todoItemsAdapter.getCount() == 0 | guardar) {
                    aInterestPoint = new InterestPoint();
                    aInterestPoint.setNombre(name);
                    aInterestPoint.setLatitud(latitud);
                    aInterestPoint.setLongitud(longitud);
                    aInterestPoint.setDescription(description);
                    aInterestPoint.setImage(image);
                    aInterestPoint.setIcon(icon);
                    aInterestPoint.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                todoItemsAdapter.notifyDataSetChanged();
                                Log.v("object saved in server:", "newParseObject()");
                                //display file saved message
                                Toast.makeText(getBaseContext(), "Punto de Interés creado correctamente!", Toast.LENGTH_SHORT).show();
                                getServerList();
                            } else {
                                Log.v("save failed, reason: " + e.getMessage(), "newParseObject()");
                                //display file saved message
                                Toast.makeText(getBaseContext(), "El Punto de Interés no se ha creado!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getBaseContext(), "Ya existe un punto de interés con ese nombre en esta zona!", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });
        this.getServerList();
    }
    public void gotoUserLocation(){
        @SuppressLint("MissingPermission") Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 12));
        } else {
            Log.v("location null", "");
        }
    }
    public void gotoDefaultLocation(){
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.8129371, -1.6465871), 12));
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Acceso a localización")//R.string.title_location_permission
                        .setMessage("Tu localización permanecerá privada y no se guardará en nuestro servidor.")//R.string.text_location_permission
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {//R.string.ok
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            if (locationEngineListener == null) {
                locationEngineListener = new LocationEngineListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected() {
                        locationEngine.requestLocationUpdates();
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                    }
                };
                locationEngine.addLocationEngineListener(locationEngineListener);
            }
            gotoUserLocation();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationEngineListener = new LocationEngineListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onConnected() {
                                locationEngine.requestLocationUpdates();
                            }

                            @Override
                            public void onLocationChanged(Location location) {

                            }
                        };
                        locationEngine.addLocationEngineListener(locationEngineListener);
                        gotoUserLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    gotoDefaultLocation();
                }
                return;
            }

        }
    }


}