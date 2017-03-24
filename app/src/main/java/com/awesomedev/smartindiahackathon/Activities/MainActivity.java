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
import com.awesomedev.smartindiahackathon.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all the views
        ButterKnife.bind(this);

        bEstimate.setOnClickListener(this);
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
