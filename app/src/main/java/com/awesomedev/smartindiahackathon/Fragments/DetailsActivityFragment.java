package com.awesomedev.smartindiahackathon.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Models.Counter;
import com.awesomedev.smartindiahackathon.Models.Route.Legs;
import com.awesomedev.smartindiahackathon.Models.Route.OverviewPolyline;
import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;
import com.awesomedev.smartindiahackathon.Models.Route.Routes;
import com.awesomedev.smartindiahackathon.R;
import com.awesomedev.smartindiahackathon.Util.RetrofitHelper;
import com.awesomedev.smartindiahackathon.Util.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.CounterEntry;
import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.FlightEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = DetailsActivityFragment.class.getSimpleName();


    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;


    @BindString(R.string.KEY_AIRPORT_ID)
    String KEY_AIRPORT_ID;

    @BindString(R.string.KEY_CARRIER_ID)
    String KEY_CARRIER_ID;

    @BindString(R.string.KEY_FLIGHT_ID)
    String KEY_FLIGHT_ID;


    @BindString(R.string.API_KEY)
    String API_KEY;

    @BindView(R.id.mv_mapview)
    MapView mapView;

    @BindView(R.id.ll_bottom_sheet_view)
    View cvBottomSheetView;

    @BindView(R.id.tv_airport)
    TextView tvAirport;

    @BindView(R.id.tv_flightNumber)
    TextView tvFlightNumber;

    @BindView(R.id.tv_source)
    TextView tvSource;

    @BindView(R.id.tv_destination)
    TextView tvDestination;


    @BindView(R.id.tv_departure_time)
    TextView tvDepartureTime;

    @BindView(R.id.tv_estimate_view)
    TextView tvEstimate;

    @BindView(R.id.fab_navigation)
    FloatingActionButton fabNavigation;

    private GoogleMap map = null;

    private String airport = null;
    private String carrier = null;
    private String flight = null;

    private int carrier_id, flight_id;


    private static final int FLIGHT_LOADER_ID = 10;
    private static final int COUNTER_LOADER_ID = 11;

    /*Constants*/
    private static final int REQUEST_RESOLVE_ERROR = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private static float base_num_mins = 0;

    private static GoogleApiClient mClient = null;
    private static DatabaseReference reference = null;
    private static FirebaseDatabase firebaseDatabase = null;

    private static ValueEventListener mListener = null;

    private static BottomSheetBehavior bottomSheetBehavior = null;

    public DetailsActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        bottomSheetBehavior = BottomSheetBehavior.from(cvBottomSheetView);
        bottomSheetBehavior.setPeekHeight(300);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        Bundle args = getArguments();

        this.airport = args.getString(KEY_AIRPORT);
        this.carrier = args.getString(KEY_CARRIER);
        this.flight = args.getString(KEY_FLIGHT);


        // Get the flight details asynchronously
        this.carrier_id = args.getInt(KEY_CARRIER_ID);
        this.flight_id = args.getInt(KEY_FLIGHT_ID);


        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference(this.airport + "/" + this.carrier + "/carrier");

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Counter> counters = new ArrayList<Counter>();
                for (DataSnapshot counterSnapshot : dataSnapshot.getChildren()) {
                    counters.add(counterSnapshot.getValue(Counter.class));
                }

                for (Counter counter : counters) {
                    ContentValues values = new ContentValues();
                    values.put(CounterEntry.COLUMN_CARRIER_KEY, carrier_id);

                    values.put(CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME, counter.getThroughput() * counter.getCounterCount());
                    values.put(CounterEntry.COLUMN_COUNTER_NUMBER, counter.getCounterNumber());
                    values.put(CounterEntry.COLUMN_COUNTER_THROUGHPUT, counter.getThroughput());
                    values.put(CounterEntry.COLUMN_COUNTER_COUNT, counter.getCounterCount());

                    getActivity().getApplicationContext().getContentResolver().insert(CounterEntry.CONTENT_URI, values);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(mListener);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fabNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                doMapStuff();
            }
        });

        Cursor rCursor = getContext().getContentResolver().query(FlightEntry.CONTENT_URI, null, null, null, null);

        rCursor.moveToFirst();
        do {
            int id = rCursor.getInt(rCursor.getColumnIndex(FlightEntry._ID));
            String flightName = rCursor.getString(rCursor.getColumnIndex(FlightEntry.COLUMN_FLIGHT_NUMBER));

            Log.d(TAG, "onCreateView: flights in db : " + flightName + " " + id);
        } while (rCursor.moveToNext());

        return rootView;
    }


    @Override
    public void onStart() {
        mapView.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                if (!mClient.isConnected() && !mClient.isConnecting()) {
                    mClient.connect();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (!map.isMyLocationEnabled())
                    map.setMyLocationEnabled(true);
                doMapStuff();
            } else {
                Toast.makeText(getActivity(), "Require location permission for finding navigation path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Play services connected , now proceed with the flow

        // Load the data from the backend
        getLoaderManager().restartLoader(FLIGHT_LOADER_ID, null, DetailsActivityFragment.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException exception) {
                exception.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Google play service is required for this application",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void updateDetailsView(Cursor data) {
        data.moveToFirst();
        tvAirport.setText(this.airport);
        tvFlightNumber.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_FLIGHT_NUMBER)));

        tvSource.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_SOURCE)));
        tvDestination.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_DESTINATION)));

        final long delayed = Long.parseLong(data.getString(data.getColumnIndex(FlightEntry.COLUMN_DELAYED)));

        final String departureTimeString = data.getString(data.getColumnIndex(FlightEntry.COLUMN_DEPARTURE_TIME));
        final long timestamp = Long.parseLong(departureTimeString);

        Date departureDatetime = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm a");

        String formattedDepartureDate = dateFormat.format(departureDatetime);
        String formattedDepartureTime = timeFormat.format(departureDatetime);

        tvDepartureTime.setText("on " + formattedDepartureDate + " at " + formattedDepartureTime);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void doMapStuff() {
        // Enable current location pointer
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Find the directions from the current location to the airport using
                    // Maps Directions API
                    String origin = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                    String destination = DetailsActivityFragment.this.airport.replace(' ', '+');
                    Call<RouteDirections> call = RetrofitHelper.getGoogleMapsServiceInstance().getDirections(origin, destination, API_KEY);
                    call.enqueue(new Callback<RouteDirections>() {
                        @Override
                        public void onResponse(Call<RouteDirections> call, Response<RouteDirections> response) {

                            // Get the directions polylines and plot on the map
                            RouteDirections directions = response.body();
                            List<Routes> routes = directions.getRoutes();

                            Routes shortestRoute = routes.get(0);
                            Legs firstLeg = shortestRoute.getLegs().get(0);

                            float travelTime = 0;
                            for ( Legs leg : shortestRoute.getLegs() ){
                                travelTime += leg.getDuration().getValue();
                            }

                            // convert travel time to minutes
                            travelTime /= 60;
                            base_num_mins = travelTime;

                            // Plot the polyline on the map
                            OverviewPolyline overviewPolyline = shortestRoute.getOverviewPolyline();
                            plotPolyline(overviewPolyline);

                            // Zoom out to the bounds
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            LatLng northeast = shortestRoute.getBounds().getNortheast().getLatLng();
                            LatLng southwest = shortestRoute.getBounds().getSouthwest().getLatLng();

                            builder.include(northeast);
                            builder.include(southwest);

                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 8));

                            if (getActivity() != null)
                                getActivity().getSupportLoaderManager().restartLoader(COUNTER_LOADER_ID, null, DetailsActivityFragment.this);

                        }

                        @Override
                        public void onFailure(Call<RouteDirections> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }

    private void plotPolyline(OverviewPolyline overviewPolyline) {
        List<LatLng> points = Utilities.decodePoly(overviewPolyline.getPoints());
        PolylineOptions lineOptions = new PolylineOptions();

        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.RED);

        map.addPolyline(lineOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_SHORT).show();
        map = googleMap;
        mClient.connect();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FLIGHT_LOADER_ID) {
            Uri uri = FlightEntry.getFlightUri(flight_id);

            return new CursorLoader(getActivity(),
                    FlightEntry.getFlightUri(flight_id),
                    null,
                    null,
                    null,
                    null
            );
        }

        if (id == COUNTER_LOADER_ID) {
            return new CursorLoader(getActivity(),
                    CounterEntry.buildCounterUri(carrier_id),
                    null,
                    null,
                    null,
                    CounterEntry.COLUMN_COUNTER_THROUGHPUT+ " DESC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == FLIGHT_LOADER_ID) {
            updateDetailsView(data);
            doMapStuff();
        }

        if (loader.getId() == COUNTER_LOADER_ID) {
            if (this.isAdded()) {
                data.moveToFirst();
                float throughput = data.getFloat(data.getColumnIndex(CounterEntry.COLUMN_COUNTER_THROUGHPUT));
                int number_of_people = data.getInt(data.getColumnIndex(CounterEntry.COLUMN_COUNTER_COUNT));

                Log.d(TAG, "onLoadFinished: " + throughput);
                float avg_waiting_time = (throughput * number_of_people)/60;
                float totalEstimatesMinutes = base_num_mins + avg_waiting_time;
                String friendlyEstimatedTimeString = Utilities.getFriendlyTimeString ( totalEstimatesMinutes );

                tvEstimate.setText(friendlyEstimatedTimeString);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
