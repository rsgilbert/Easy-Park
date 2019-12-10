package com.learning.one_pc.easypark;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RemoveParkingFragment extends Fragment {
    private ListView listview;
    private GeoFire geoFire;
    private DatabaseReference mRef;
    private ArrayList<String> parksToRemove;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userID;
    private ArrayAdapter<String> adapter;
    ArrayList<String> children;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_removeparking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        geoFire = new GeoFire(mRef);
        parksToRemove = new ArrayList<>();
        listview = view.findViewById(R.id.listview);
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        children = new ArrayList<>();


        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren().iterator();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object val = dataSnapshot.getValue();
                if(dataSnapshot.child(userID).getChildren().iterator().hasNext()){
                    children.add(dataSnapshot.child(userID).getChildren().iterator().next().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        adapter = new ArrayAdapter<>(getActivity(), andr)

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                geoFire.removeLocation(listview.getItemAtPosition(i).toString());
            }
        });

    }
}
