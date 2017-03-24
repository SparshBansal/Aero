package com.awesomedev.smartindiahackathon.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.awesomedev.smartindiahackathon.Fragments.DetailsActivityFragment;
import com.awesomedev.smartindiahackathon.R;

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

    Fragment detailsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Log.d(TAG, "onCreate: " + this.KEY_AIRPORT);

        Bundle args = new Bundle();

        // Put the data in the bundle
        args.putString(KEY_AIRPORT,getIntent().getStringExtra(KEY_AIRPORT));
        args.putString(KEY_CARRIER,getIntent().getStringExtra(KEY_CARRIER));
        args.putString(KEY_FLIGHT,getIntent().getStringExtra(KEY_FLIGHT));

        detailsFragment = new DetailsActivityFragment();
        detailsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_container,detailsFragment).commit();
    }

}
