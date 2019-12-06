package com.learning.one_pc.easypark;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NearbyParksFragment extends Fragment {
    private ListView listview;
    private ArrayList<String> myarray;
    private ArrayList<Object> objectArray;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private FirebaseDatabase database;
    public GeoFire geoFire;
    private FirebaseUser user;
    private ArrayAdapter adapter;
    private GeoQuery geoQuery;
    public static String loc;

    //location
    private static final String FLOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CLOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static String[] permissions = {FLOCATION, CLOCATION};
    public static boolean locationPermissions = false;
    private static final int REQUEST_LOCATION_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static LatLng myPosition;
    public static LatLng locationClicked;
    public static String nameOfLocationClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_nearbyparks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = view.findViewById(R.id.listview);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        geoFire = new GeoFire(mRef);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        objectArray = new ArrayList<>();

        myarray = new ArrayList<>();
        Values values = new Values();
        mActivity();


    }

    public void mActivity() {
        Values vals = new Values();
        myarray.add("The following parks are around you in a radius of " + vals.getRadius());
        if (checkNetwork(getActivity())) {   //check if getActivity could be replaced with getContext in case of error while running
            if (isLocationEnabled(getActivity())) {
                getLocationPermissions();
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                try {
                    if(locationPermissions) {
                        Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(
                                new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Location location = (Location) task.getResult();
                                            myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                            final Values vals = new Values();
                                            if (myPosition != null) {
                                                geoQuery = geoFire.queryAtLocation(new GeoLocation(myPosition.latitude, myPosition.longitude), vals.getRadius());
                                                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                                    @Override
                                                    public void onKeyEntered(String key, GeoLocation location) {
                                                        myarray.add(key);
                                                        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myarray);
                                                        listview.setAdapter(adapter);
                                                    }

                                                    @Override
                                                    public void onKeyExited(String key) {
                                                        myarray.remove(key);
                                                        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myarray);
                                                        listview.setAdapter(adapter);
                                                    }

                                                    @Override
                                                    public void onKeyMoved(String key, GeoLocation location) {

                                                    }

                                                    @Override
                                                    public void onGeoQueryReady() {

                                                    }

                                                    @Override
                                                    public void onGeoQueryError(DatabaseError error) {

                                                    }

                                                });
                                            } else
                                                Toast.makeText(getActivity(), "no position", Toast.LENGTH_SHORT).show();
                                            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myarray);
                                            listview.setAdapter(adapter);
                                        } else if (!task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "setting location failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
                    }
                    else Toast.makeText(getActivity(), "Location permissions not given", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }


                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        loc = listview.getItemAtPosition(i).toString();
                        nameOfLocationClicked = loc;

                        geoFire.getLocation(nameOfLocationClicked, new LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation location) {
                                if (location == null) {
                                    Toast.makeText(getActivity(), "Parking not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    locationClicked = new LatLng(location.latitude, location.longitude);
                                    startActivity(new Intent(getActivity(), MapActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            } else
                Toast.makeText(getActivity(), "Enable location and try again", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();

    }

    private static Boolean checkNetwork(Context mContext) {
        NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void getLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), FLOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), CLOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissions = true;
                getDeviceLocation();
            } else
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
        } else
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
    }

    public void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(
                    new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location location = (Location) task.getResult();
                                myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            } else if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "setting location failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissions = true;
        switch (requestCode) {
            case REQUEST_LOCATION_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissions = false;
                            return;
                        }
                    }
                    Toast.makeText(getActivity(), "permissions are now granted", Toast.LENGTH_LONG).show();
                    mActivity();

                }
            }
        }
    }

}
