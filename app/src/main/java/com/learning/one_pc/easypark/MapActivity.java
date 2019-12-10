package com.learning.one_pc.easypark;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.learning.one_pc.easypark.Models.PlaceAutocompleteAdapter;
import com.learning.one_pc.easypark.Models.PlaceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MapActivity";
    private static final String FLOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CLOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static String[] permissions = {FLOCATION, CLOCATION};
    public boolean locationPermissions;
    private static final int REQUEST_LOCATION_CODE = 1234;
    private GoogleMap gMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private PlaceAutocompleteAdapter oPlaceAutocompleteAdapter;
    private GoogleApiClient oGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private GeoDataClient oGeoDataClient, pGeoDataClient;
    private PlaceInfo oPlaceInfo;
    //widgets
    private AutoCompleteTextView searchbar;
    private ImageView oGps;
    private Context context;
    public static LatLng myPosition = new LatLng(0.3, 33);

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No connection"+ connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private static Boolean checkNetwork(Context mContext){
        NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if(info == null || !info.isConnected()){
            return false;
        }
        if(info.isRoaming()){
            return true;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        searchbar = findViewById(R.id.edittext);
        oGps = findViewById(R.id.gps);

        if(checkNetwork(MapActivity.this)) {
            if (isLocationEnabled(MapActivity.this)) {
                mActivity();
            } else Toast.makeText(this, "Location not set", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();


    }
    private void mActivity(){
        try {
            getLocationPermissions();
        }
        catch (Exception e){
            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(locationPermissions){
            initialiseMap();
        }
    }

    private void getLocationPermissions(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FLOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), CLOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissions = true;
            }
            else
                ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_CODE);
        }
        else
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_CODE);
    }
    private void initialiseMap(){
        SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapfragment.getMapAsync(MapActivity.this);
    }


    private void init(){
        NearbyParksFragment npf = new NearbyParksFragment();
        if(npf.locationClicked != null && npf.nameOfLocationClicked != null) {
            MarkerOptions options = new MarkerOptions();
            options.title(npf.nameOfLocationClicked)
                    .position(npf.locationClicked);
            gMap.addMarker(options);
        }
        oGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        oGeoDataClient = Places.getGeoDataClient(this, null);




        oPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, oGeoDataClient, LAT_LNG_BOUNDS, null);
        searchbar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchbar.setText("");
                    }
                }
        );
        searchbar.setOnItemClickListener(oAutocompleteClickListener);
        searchbar.setAdapter(oPlaceAutocompleteAdapter);
        searchbar.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    //make sure you have  android:inputType="text" in xml file
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if(actionId == EditorInfo.IME_NULL
                                && keyEvent.getAction() == KeyEvent.ACTION_DOWN){

                            geoLocate();
                        }

                        return false;
                    }
                }
        );
        oGps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDeviceLocation();
                        searchbar.setText(myPosition.toString());

                    }
                }
        );
    }
    private void geoLocate(){

        String searchInput = searchbar.getText().toString();
        Geocoder geo = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try{

            list = geo.getFromLocationName(searchInput, 1);
            Toast.makeText(this, "trying location", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            Toast.makeText(this, "geoLocate:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(list.size() > 0){
            Toast.makeText(this, "list size > 0", Toast.LENGTH_SHORT).show();
            Address address = list.get(0);
            Toast.makeText(this, "got Address at: " + address.toString(), Toast.LENGTH_LONG).show();
            moveCam(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
        else Toast.makeText(this, "size smaller than 0", Toast.LENGTH_SHORT).show();
    }

    public void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationPermissions){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Location location = (Location) task.getResult();
                                    myPosition =  new LatLng(location.getLatitude(), location.getLongitude());
                                    moveCam(myPosition, DEFAULT_ZOOM, "My Position");
                                }
                            }
                        }
                );
            }

        }catch (SecurityException e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private  void moveCam(LatLng latLng, float zoom, String title){
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //marker object

        if(!title.equals("My Position")) {
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            gMap.addMarker(marker);
        }
    }
    //overiding method moveCam..
    private  void moveCam(LatLng latLng, float zoom, PlaceInfo placeInfo){
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        gMap.clear();

        try {
            String snippet = placeInfo.getPhoneNumber();
            if(placeInfo.getLots() != null){
                snippet = placeInfo.getPhoneNumber()+ placeInfo.getLots();
            }

            MarkerOptions options = new MarkerOptions()
                    .title(placeInfo.getName())
                    .position(latLng)
                    .snippet(snippet);

            gMap.addMarker(options);
        } catch (NullPointerException e) {
            Toast.makeText(this, "NullPointerException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissions = false;
        switch (requestCode){
            case REQUEST_LOCATION_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissions = false;
                            return;
                        }
                    }
                    Toast.makeText(this, "permissions are granted for the first time", Toast.LENGTH_LONG).show();
                    //initialise map
                    mActivity();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if(locationPermissions){
            getDeviceLocation();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;

            }
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
            mSetPoiClick(gMap);
        }
    }


    private void mSetPoiClick(final GoogleMap map){
        map.setOnPoiClickListener(
                new GoogleMap.OnPoiClickListener() {
                    @Override
                    public void onPoiClick(PointOfInterest pointOfInterest) {
                        map.clear();
                        Marker poiMarker = map.addMarker(
                                new MarkerOptions()
                                        .position(pointOfInterest.latLng)
                                        .title(pointOfInterest.name)
                        );
                        poiMarker.showInfoWindow();
                        searchbar.setText(pointOfInterest.latLng.toString());

                    }
                }
        );
    }


    //hide keyboard on selection
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    // google places API autocomplete suggestions
    private AdapterView.OnItemClickListener oAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            final AutocompletePrediction item = oPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(oGoogleApiClient, placeId);
            placeResult.setResultCallback(oUpdatePlaceDetailsCallback);
        }
    };  //dont forget about the semicolon

    private ResultCallback<PlaceBuffer> oUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Toast.makeText(MapActivity.this, "Place query unsuccessful: "+ places.getStatus().toString(), Toast.LENGTH_SHORT).show();
                places.release();
                return;
            }
            final Place place = places.get(0);
            oPlaceInfo = new PlaceInfo();
            oPlaceInfo.setAddress(place.getAddress().toString());
            oPlaceInfo.setLatLng(place.getLatLng());
            oPlaceInfo.setName(place.getName().toString());
            if(place.getPhoneNumber() != null)
                oPlaceInfo.setPhoneNumber(place.getPhoneNumber().toString());


            moveCam(oPlaceInfo.getLatLng(), DEFAULT_ZOOM, oPlaceInfo);
            places.release();
        }
    };

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

}