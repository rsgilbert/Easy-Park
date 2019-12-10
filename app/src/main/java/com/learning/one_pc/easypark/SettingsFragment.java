package com.learning.one_pc.easypark;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsFragment extends Fragment{

    private TextView radius;
    private Button setradius, restoredefaults;
    private EditText newradius;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radius = view.findViewById(R.id.radius);
        setradius= view.findViewById(R.id.setradius);
        newradius = view.findViewById(R.id.newradius);
        restoredefaults = view.findViewById(R.id.defaultradius);

        final Values values = new Values();
        Double dRadius = values.getRadius();
        final String sRadius = dRadius.toString();

        radius.setText(sRadius);

        setradius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newradius.getText().toString().equals("")){
                    Values vals = new Values();
                    vals.setRadius(Double.parseDouble(newradius.getText().toString()));
                    Double dRad = vals.getRadius();
                    radius.setText(dRad.toString());
                }
            }
        });

        restoredefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Values vals = new Values();
                Defaults defaults = new Defaults();
                vals.setRadius(defaults.getRadius());
                Values newValues = new Values();
                Double dRad = newValues.getRadius();
                radius.setText(dRad.toString());
            }
        });

    }
}
