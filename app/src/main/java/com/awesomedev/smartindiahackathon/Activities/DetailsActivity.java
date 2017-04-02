package com.awesomedev.smartindiahackathon.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.awesomedev.smartindiahackathon.Fragments.DetailsActivityFragment;
import com.awesomedev.smartindiahackathon.R;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindString;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;


    private static final String KEY_AIRPORT_ID = "AIRPORT_ID";
    private static final String KEY_CARRIER_ID = "CARRIER_ID";

    Fragment detailsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Log.d(TAG, "onCreate: onCreateActivity is called");

        if (savedInstanceState == null)
            Log.d(TAG, "onCreate: savedInstanceState is null");
        else
            Log.d(TAG, "onCreate: savedInstanceState is not null");

        Bundle args = new Bundle();

        // Put the data in the bundle
        args.putString(KEY_AIRPORT,getIntent().getStringExtra(KEY_AIRPORT));
        args.putString(KEY_CARRIER,getIntent().getStringExtra(KEY_CARRIER));
        args.putString(KEY_FLIGHT,getIntent().getStringExtra(KEY_FLIGHT));

        args.putInt(KEY_AIRPORT_ID,getIntent().getIntExtra(KEY_AIRPORT_ID,0));
        args.putInt(KEY_CARRIER_ID,getIntent().getIntExtra(KEY_CARRIER_ID,0));

        detailsFragment = new DetailsActivityFragment();
        detailsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container,detailsFragment).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity Destroyed");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();
    }
}
