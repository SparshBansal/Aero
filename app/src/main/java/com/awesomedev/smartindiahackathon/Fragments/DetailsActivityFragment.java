package com.awesomedev.smartindiahackathon.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Models.FlightDetails;
import com.awesomedev.smartindiahackathon.Models.Route.Legs;
import com.awesomedev.smartindiahackathon.Models.Route.OverviewPolyline;
import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;
import com.awesomedev.smartindiahackathon.Models.Route.Routes;
import com.awesomedev.smartindiahackathon.R;
import com.awesomedev.smartindiahackathon.Util.RetrofitHelper;
import com.awesomedev.smartindiahackathon.Util.Utilities;
import com.awesomedev.smartindiahackathon.Views.CustomMapView;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {


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

    @BindView(R.id.sv_content_view)
    ScrollView scrollView;

    private GoogleMap map = null;

    private String airport = null;
    private String carrier = null;
    private String flight = null;


    /*Constants*/
    private static final int REQUEST_PERMISSION = 100;
    private static final int REQUEST_RESOLVE_ERROR = 101;

    private static GoogleApiClient mClient = null;

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
        // fetchFlightDetails(airport,carrier,flight);

        // Sets up consistent looking scrolling behaviour
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                int basePadding = Utilities.dpToPx(8);
//                int paddingTop = basePadding;
//
//                if (basePadding - scrollView.getScrollY() < 0)
//                    paddingTop = 0;
//                else
//                    paddingTop = basePadding - scrollView.getScrollY();
//
//                scrollView.setPadding(basePadding, paddingTop, basePadding, basePadding);
//            }
//        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    void buildApi(){
        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    void initMapAndApiClient() {
        if (map == null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d(TAG, "onMapReady: ");

                    map = googleMap;
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    initApiClient();
                }
            });
        } else {
            initApiClient();
        }
    }

    void initApiClient() {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

        }
        Log.d(TAG, "initApiClient: initApiClient Called");
        mClient.connect();
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
        doMapStuff();
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


    private void fetchFlightDetails(String airport, String carrier, String flight) {
        Call<FlightDetails> call = RetrofitHelper.getFlightServiceInstance().getFlightDetails(airport, carrier, flight);
        call.enqueue(new Callback<FlightDetails>() {

            // Successful response , handle the details
            @Override
            public void onResponse(Call<FlightDetails> call, Response<FlightDetails> response) {
                FlightDetails details = response.body();

                // Update details view
                updateDetailsView(details);
            }

            @Override
            public void onFailure(Call<FlightDetails> call, Throwable t) {
                // Some failure occurred , inform the user
                Toast.makeText(getActivity(), "Some error occurred, Please retry",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDetailsView(FlightDetails flightDetails) {

    }


    private void doMapStuff() {
        // Initialize for camera activities
        MapsInitializer.initialize(getActivity());

        // Enable current location pointer
        map.setMyLocationEnabled(true);

        // Get the current location and zoom the camera towards it
        Location currentLocation = getCurrentLocation(getActivity());
        if (currentLocation != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15);
            map.animateCamera(cameraUpdate);


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

                    Toast.makeText(getActivity(), "Estimated Time : " + firstLeg.getDuration().getText(), Toast.LENGTH_SHORT).show();

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

                    int padding = 50;

                    CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,padding);
                    map.animateCamera(update);
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
}
