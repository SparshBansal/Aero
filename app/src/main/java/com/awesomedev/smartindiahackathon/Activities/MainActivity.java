package com.awesomedev.smartindiahackathon.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Activities.DetailsActivity;
import com.awesomedev.smartindiahackathon.Models.Counter;
import com.awesomedev.smartindiahackathon.Models.FlightDetails;
import com.awesomedev.smartindiahackathon.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_airport)
    EditText etAirport;

    @BindView(R.id.et_carrier)
    EditText etCarrier;

    @BindView(R.id.et_flight)
    EditText etFlight;

    @BindView(R.id.b_estimate)
    Button bEstimate;

    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;

    // Database Reference for accessing firebase application
    private static DatabaseReference reference = null;
    private static FirebaseDatabase firebaseDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all the views
        ButterKnife.bind(this);

        bEstimate.setOnClickListener(this);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot airportSnapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot carrierSnapshot : airportSnapshot.getChildren()){
                        for (DataSnapshot  mSnapshot : carrierSnapshot.getChildren()){
                            if (mSnapshot.getKey().equals("flight")){
                                List<FlightDetails> flights = new ArrayList<FlightDetails>();
                                for (DataSnapshot flight : mSnapshot.getChildren()) {
                                    FlightDetails flightDetails = flight.getValue(FlightDetails.class);
                                    flights.add(flightDetails);
                                    Log.d(TAG, "onDataChange: " + flightDetails.getFlightNo());
                                }
                            }
                            else if (mSnapshot.getKey().equals("carrier")){
                                List<Counter> counters = new ArrayList<Counter>();
                                for (DataSnapshot counterSnapshot : mSnapshot.getChildren()){
                                    counters.add(counterSnapshot.getValue(Counter.class));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_estimate:

                String airport = etAirport.getText().toString();
                String carrier = etCarrier.getText().toString();
                String flightNumber = etFlight.getText().toString();

                Log.d(TAG, "onClick: airport : " + airport);
                Log.d(TAG, "onClick: carrier : " + carrier);
                Log.d(TAG, "onClick: flight : " + flightNumber);

                if (airport.equals("")||carrier.equals("")||flightNumber.equals("")){
                    Toast.makeText(this, "None of the fields should be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this,DetailsActivity.class);
                intent = intent.putExtra(KEY_AIRPORT,airport)
                        .putExtra(KEY_CARRIER,carrier)
                        .putExtra(KEY_FLIGHT,flightNumber);
                startActivity(intent);

                break;
        }
    }
}
