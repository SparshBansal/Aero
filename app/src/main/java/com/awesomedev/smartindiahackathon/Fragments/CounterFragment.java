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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awesomedev.smartindiahackathon.CustomViews.CounterConstraintView;
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
public class CounterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = CounterFragment.class.getSimpleName();


    @BindView(R.id.ll_counter_views)
    LinearLayout llCounterViews;

    @BindView(R.id.tv_moveCounter)
    TextView tvMoveCounter;

    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;

    @BindString(R.string.KEY_CARRIER_ID)
    String KEY_CARRIER_ID;

    @BindString(R.string.KEY_AIRPORT_ID)
    String KEY_AIRPORT_ID;


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

    boolean firstTime = true;
    List<CounterConstraintView> counterViews = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this, rootView);


        this.airport = getActivity().getIntent().getStringExtra(KEY_AIRPORT);
        this.carrier = getActivity().getIntent().getStringExtra(KEY_CARRIER);
        this.flight = getActivity().getIntent().getStringExtra(KEY_FLIGHT);


        // Get the flight details asynchronously
        carrier_id = getActivity().getIntent().getIntExtra(KEY_CARRIER_ID, 0);

        Log.d(TAG, "onCreateView:  " + Integer.toString(carrier_id));

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference(this.airport + "/" + this.carrier + "/carrier");

        getLoaderManager().restartLoader(COUNTER_LOADER_ID, null, CounterFragment.this);

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

                    values.put(DatabaseContract.CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME, counter.getThroughput() * counter.getCounterCount());
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
                    DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER+ " ASC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: " + data.getCount());

        if ( data.getCount() > 0 ){

            int minCounterNumber = 0;
            float minWt = Integer.MAX_VALUE;


            if ( firstTime ) {
                firstTime = false;
                data.moveToFirst();

                counterViews = new ArrayList<>();

                do {

                    int counterCount = data.getInt(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_COUNT));
                    float throughput = data.getFloat(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_THROUGHPUT));

                    CounterConstraintView counterConstraintView = new CounterConstraintView(getActivity());
                    counterConstraintView.setCount(counterCount);
                    counterConstraintView.setAverageWaitingTime(throughput);

                    float avgWt = throughput*counterCount;
                    if ( avgWt < minWt ) {
                        minWt = avgWt;
                        minCounterNumber = data.getInt(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER));
                    }

                    // add the views
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    llCounterViews.addView(counterConstraintView, layoutParams);
                    counterViews.add(counterConstraintView);

                } while (data.moveToNext());
            }

            else  {

                int idx = 0;
                data.moveToFirst();
                do {
                    CounterConstraintView counterConstraintView = counterViews.get(idx);
                    int counterCount = data.getInt(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_COUNT));
                    int prevCounterCount = counterConstraintView.getCount();

                    if ( prevCounterCount > counterCount ) {
                        counterConstraintView.removeElements(prevCounterCount - counterCount);
                    }

                    else if ( prevCounterCount < counterCount ){
                        counterConstraintView.addElements(counterCount - prevCounterCount);
                    }

                    float throughput = data.getFloat(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_THROUGHPUT));
                    counterConstraintView.setAverageWaitingTime(throughput);

                    float avgWt = throughput*counterCount;
                    if ( avgWt < minWt ) {
                        minWt = avgWt;
                        minCounterNumber = data.getInt(data.getColumnIndex(DatabaseContract.CounterEntry.COLUMN_COUNTER_NUMBER));
                    }

                    idx++;

                } while (data.moveToNext());
            }

            tvMoveCounter.setText(String.format("%d", minCounterNumber));
        }

        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
