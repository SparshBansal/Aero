package com.awesomedev.smartindiahackathon.Fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.awesomedev.smartindiahackathon.CustomViews.CounterViews;
import com.awesomedev.smartindiahackathon.Data.DatabaseContract;
import com.awesomedev.smartindiahackathon.Models.Counter;
import com.awesomedev.smartindiahackathon.R;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CounterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final String KEY_AIRPORT_ID = "AIRPORT_ID";
    private static final String KEY_CARRIER_ID = "CARRIER_ID";
    private static final String TAG = CounterFragment.class.getSimpleName();

    CounterViews mView = null;

    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;

    @BindView(R.id.tv_moveCounter)
    TextView tvMoveCounter;

    private String airport = null;
    private String carrier = null;
    private String flight = null;

    private int carrier_id;

    private static DatabaseReference reference = null;
    private static FirebaseDatabase firebaseDatabase = null;

    private static final int COUNTER_LOADER_ID = 11;

    public CounterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        mView = (CounterViews) rootView.findViewById(R.id.custom_view);
        ButterKnife.bind(this,rootView);


        this.airport = getActivity().getIntent().getStringExtra(KEY_AIRPORT);
        this.carrier = getActivity().getIntent().getStringExtra(KEY_CARRIER);
        this.flight = getActivity().getIntent().getStringExtra(KEY_FLIGHT);


        // Get the flight details asynchronously
        carrier_id = getActivity().getIntent().getIntExtra(KEY_CARRIER_ID,0);

        Log.d(TAG, "onCreateView:  " + Integer.toString(carrier_id) );

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference(this.airport + "/" + this.carrier + "/carrier");

        Cursor mCursor =getActivity().getContentResolver().query(DatabaseContract.CounterEntry.buildCounterUri(carrier_id),null,null,null,null);
        mView.setData(mCursor);
        getLoaderManager().restartLoader(COUNTER_LOADER_ID,null,CounterFragment.this);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Counter> counters = new ArrayList<Counter>();
                for (DataSnapshot counterSnapshot : dataSnapshot.getChildren()) {
                    counters.add(counterSnapshot.getValue(Counter.class));
                }

                for (Counter counter : counters) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.CounterEntry.COLUMN_CARRIER_KEY, carrier_id);

                    values.put(DatabaseContract.CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME, counter.getThroughput()*counter.getCounterCount());
                    values.put(DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER, counter.getCounterNumber());
                    values.put(DatabaseContract.CounterEntry.COLUMN_COUNTER_THROUGHPUT, counter.getThroughput());
                    values.put(DatabaseContract.CounterEntry.COLUMN_COUNTER_COUNT, counter.getCounterCount());

                    getActivity().getContentResolver().insert(DatabaseContract.CounterEntry.CONTENT_URI, values);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == COUNTER_LOADER_ID) {
            return new CursorLoader(getActivity(),
                    DatabaseContract.CounterEntry.buildCounterUri(carrier_id),
                    null,
                    null,
                    null,
                    DatabaseContract.CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME + " ASC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: " + data.getCount());
        mView.setData(data);

        data.moveToFirst();
        String mini = data.getString(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME));
        String counter = data.getString(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER));
        while(data.moveToNext()){
            String currentMini = data.getString(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME));
            String counterMini = data.getString(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER));

            if (Float.parseFloat(currentMini) < Float.parseFloat(mini))
                counter = counterMini;
        }

        tvMoveCounter.setText(counter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
