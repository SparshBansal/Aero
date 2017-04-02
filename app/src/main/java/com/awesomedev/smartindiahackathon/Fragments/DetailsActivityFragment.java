package com.awesomedev.smartindiahackathon.Fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Data.DatabaseContract;
import com.awesomedev.smartindiahackathon.Models.Counter;
import com.awesomedev.smartindiahackathon.Models.FlightDetails;
import com.awesomedev.smartindiahackathon.Models.Route.Legs;
import com.awesomedev.smartindiahackathon.Models.Route.OverviewPolyline;
import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;
import com.awesomedev.smartindiahackathon.Models.Route.Routes;
import com.awesomedev.smartindiahackathon.R;
import com.awesomedev.smartindiahackathon.Util.RetrofitHelper;
import com.awesomedev.smartindiahackathon.Util.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;

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

    @BindString(R.string.API_KEY)
    String API_KEY;

    @BindView(R.id.mv_mapview)
    MapView mapView;

    @BindView(R.id.cv_flight_details_view)
    CardView cardView;

    @BindView(R.id.tv_airport)
    TextView tvAirport;

    @BindView(R.id.tv_flightNumber)
    TextView tvFlightNumber;

    @BindView(R.id.tv_source)
    TextView tvSource;

    @BindView(R.id.tv_destination)
    TextView tvDestination;

    @BindView(R.id.tv_delayedView)
    TextView tvDelayed;

    @BindView(R.id.tv_departure_time)
    TextView tvDepartureTime;

    @BindView(R.id.tv_estimateView)
    TextView tvEstimate;

    private GoogleMap map = null;

    private String airport = null;
    private String carrier = null;
    private String flight = null;

    private int carrier_id;

    private static boolean isAnimating = false;


    private static final String KEY_AIRPORT_ID = "AIRPORT_ID";
    private static final String KEY_CARRIER_ID = "CARRIER_ID";

    private static final int FLIGHT_LOADER_ID = 10;
    private static final int COUNTER_LOADER_ID = 11;

    /*Constants*/
    private static final int REQUEST_PERMISSION = 100;
    private static final int REQUEST_RESOLVE_ERROR = 101;

    private static float base_num_mins = 0;

    private static GoogleApiClient mClient = null;
    private static DatabaseReference reference = null;
    private static FirebaseDatabase firebaseDatabase = null;

    private static ValueEventListener mListener = null;

    public DetailsActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        Log.d(TAG, "onCreateView: onCreateFragment Called");
        buildApi();

        Bundle args = getArguments();

        this.airport = args.getString(KEY_AIRPORT);
        this.carrier = args.getString(KEY_CARRIER);
        this.flight = args.getString(KEY_FLIGHT);


        // Get the flight details asynchronously
        this.carrier_id = args.getInt(KEY_CARRIER_ID);

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

                    values.put(CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME, counter.getAvgWaitingTime());
                    values.put(CounterEntry.COLUMN_COUNTER_NUMBER, counter.getCounterNumber());
                    values.put(CounterEntry.COLUMN_COUNTER_THROUGHPUT, counter.getThroughput());
                    values.put(CounterEntry.COLUMN_COUNTER_COUNT, counter.getCounterCount());

                    getContext().getContentResolver().insert(CounterEntry.CONTENT_URI, values);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(mListener);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        return rootView;
    }

    void buildApi() {
        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: onStartFragment is called");
        mapView.onStart();
        mClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: onStopFragment is called");
        mapView.onStop();
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: onPauseFragment is called");
        mapView.onPause();
        reference.removeEventListener(mListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: onResumeFragment is called");
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: onDestroyFragment is called");
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
        Log.d(TAG, "onSaveInstanceState: onSaveInstanceStateFragment is called");
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: onDestroyViewFragment is called");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doMapStuff();
            } else {
                Toast.makeText(getActivity(), "Sorry , the application requires to Access your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: onClientConnected Called");
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
        tvAirport.setText(data.getString(data.getColumnIndex(AirportEntry.COLUMN_AIRPORT_NAME)));
        tvSource.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_SOURCE)));
        tvDestination.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_DESTINATION)));
        tvDelayed.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_DELAYED)));
        tvFlightNumber.setText(data.getString(data.getColumnIndex(FlightEntry.COLUMN_FLIGHT_NUMBER)));

        final String departureTimeString = data.getString(data.getColumnIndex(FlightEntry.COLUMN_DEPARTURE_TIME));
        final long timestamp = Long.parseLong(departureTimeString);

        Date departureDatetime = new Date(timestamp);
        tvDepartureTime.setText(departureDatetime.toString());
    }


    private void doMapStuff() {
        // Initialize for camera activities
        MapsInitializer.initialize(getActivity());

        // Enable current location pointer
        map.setMyLocationEnabled(true);

        // Get the current location and zoom the camera towards it
        Location currentLocation = getCurrentLocation(getActivity());
        if (currentLocation != null) {

            // Find the directions from the current location to the airport using
            // Maps Directions API
            String origin = Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude());
            String destination = this.airport.replace(' ', '+');
            Log.d(TAG, "doMapStuff: " + "Map stuff called");
            Call<RouteDirections> call = RetrofitHelper.getGoogleMapsServiceInstance().getDirections(origin, destination, API_KEY);
            Log.d(TAG, "doMapStuff: " + call.request().url().toString());
            call.enqueue(new Callback<RouteDirections>() {
                @Override
                public void onResponse(Call<RouteDirections> call, Response<RouteDirections> response) {
                    Log.d(TAG, "onResponse: Response received");
                    Log.d(TAG, "onResponse: " + call.request().url().toString());

                    // Get the directions polylines and plot on the map
                    RouteDirections directions = response.body();
                    List<Routes> routes = directions.getRoutes();

                    Routes shortestRoute = routes.get(0);
                    Legs firstLeg = shortestRoute.getLegs().get(0);

                    float estimatedTime = firstLeg.getDuration().getValue();
                    float estimatedDistance = firstLeg.getDistance().getValue();

                    base_num_mins = base_num_mins + estimatedTime + 45;

                    // Plot the polyline on the map
                    OverviewPolyline overviewPolyline = shortestRoute.getOverviewPolyline();
                    plotPolyline(overviewPolyline);

                    // Zoom out to the bounds
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    LatLng northeast = shortestRoute.getBounds().getNortheast().getLatLng();
                    LatLng southwest = shortestRoute.getBounds().getSouthwest().getLatLng();

                    builder.include(northeast);
                    builder.include(southwest);

                    LatLngBounds bounds = builder.build();

                    int paddingBottom = cardView.getHeight();
                    int paddingSides = Utilities.dpToPx(48);

                    map.setPadding(0, 0, 0, paddingBottom);

                    CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, paddingSides);
                    map.animateCamera(update);

                    if (getActivity() != null)
                        getActivity().getSupportLoaderManager().restartLoader(COUNTER_LOADER_ID, null, DetailsActivityFragment.this);

                    runCardViewAnimation();
                }

                @Override
                public void onFailure(Call<RouteDirections> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + call.request().url().toString());
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } else {
            Log.d(TAG, "doMapStuff: " + "current location is null");
        }
    }


    public Location getCurrentLocation(Context context) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (mLastLocation == null) {
            Toast.makeText(context, "Last Location is null", Toast.LENGTH_SHORT).show();
        }
        return mLastLocation;
    }

    private void runCardViewAnimation() {
        cardView.setTranslationY(cardView.getHeight());
        cardView.setVisibility(View.VISIBLE);
        cardView.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .setDuration(800)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void plotPolyline(OverviewPolyline overviewPolyline) {
        List<LatLng> points = decodePoly(overviewPolyline.getPoints());
        PolylineOptions lineOptions = new PolylineOptions();

        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.RED);

        map.addPolyline(lineOptions);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_SHORT).show();
        map = googleMap;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FLIGHT_LOADER_ID) {
            return new CursorLoader(getActivity(),
                    FlightEntry.getFlightUri(carrier_id),
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
                    CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME + " ASC"
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
                float avg_waiting_time = Float.parseFloat(data.getString(data.getColumnIndex(CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME)));
                int number_of_people = Integer.parseInt(data.getString(data.getColumnIndex(CounterEntry.COLUMN_COUNTER_COUNT)));
                int counter_number = Integer.parseInt(data.getString(data.getColumnIndex(CounterEntry.COLUMN_COUNTER_NUMBER)));
                Log.d(TAG, "onLoadFinished: " + Integer.toString(counter_number));
                tvEstimate.setText(String.format("%f hours", (base_num_mins + (avg_waiting_time * number_of_people)) / 60));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
