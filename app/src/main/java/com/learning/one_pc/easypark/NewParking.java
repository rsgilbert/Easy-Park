package com.learning.one_pc.easypark;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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

public class NewParking extends Fragment {
    private Button submit, getlocation;
    private TextView loc1, loc2;
    private EditText parkname;
    private static final String FLOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CLOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static String[] permissions = {FLOCATION, CLOCATION};
    public boolean locationPermissions;
    private static final int REQUEST_LOCATION_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng myPosition;
    private GeoFire geoFire;
    private String sParkname;
    private LatLng myLocation;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_newparking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parkname = view.findViewById(R.id.parkname);
        loc1 = view.findViewById(R.id.loc1);
        loc2 = view.findViewById(R.id.loc2);
        submit = view.findViewById(R.id.submit);
        getlocation = view.findViewById(R.id.getlocation);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        user = mAuth.getCurrentUser();
        geoFire = new GeoFire(mRef);


        getlocation.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View view) {
                                               try {
                                                   if (checkNetwork(getActivity())) {   //check if getActivity could be replaced with getContext in case of error while running
                                                       if (isLocationEnabled(getActivity())) {
                                                           getLocationPermissions();
                                                           getDeviceLocation();
                                                       } else
                                                           Toast.makeText(getActivity(), "Enable location and try again", Toast.LENGTH_SHORT).show();
                                                   } else
                                                       Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                                               } catch (Exception e) {
                                                   Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
        );
        submit.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                sParkname = parkname.getText().toString();
                if (!sParkname.equals("")) {
                    if (checkNetwork(getActivity())) {   //check if getActivity could be replaced with getContext in case of error while running
                        if (isLocationEnabled(getActivity())) {
                            geoFire.getLocation(sParkname, new LocationCallback() {
                                @Override
                                public void onLocationResult(String key, GeoLocation location) {
                                    if (location == null) {
                                        if (myPosition != null) {
                                            geoFire.setLocation(sParkname, new GeoLocation(Double.parseDouble(loc1.getText().toString()), Double.parseDouble(loc2.getText().toString()))
                                                    , new GeoFire.CompletionListener() {
                                                        @Override
                                                        public void onComplete(String key, DatabaseError error) {
                                                            Toast.makeText(getActivity(), "Parking " + sParkname + " has been added successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else
                                            Toast.makeText(getActivity(), "No location", Toast.LENGTH_SHORT).show();
                                    } else if (location != null) {
                                        Toast.makeText(getActivity(), key + " already exists", Toast.LENGTH_SHORT).show();
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else
                        Toast.makeText(getActivity(), "Enable location and try again", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }

        });

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
            } else
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
        } else
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
    }

    public void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (locationPermissions) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Location location = (Location) task.getResult();
                                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                    loc1.setText(((Double) myPosition.latitude).toString());
                                    loc2.setText(((Double) myPosition.longitude).toString());
                                }
                            }
                        }
                );
            }

        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                    getDeviceLocation();
                }
            }
        }
    }
}

