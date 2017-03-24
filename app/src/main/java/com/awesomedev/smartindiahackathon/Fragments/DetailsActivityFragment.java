package com.awesomedev.smartindiahackathon.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Models.FlightDetails;
import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;
import com.awesomedev.smartindiahackathon.R;
import com.awesomedev.smartindiahackathon.Util.RetrofitHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {


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

    private GoogleMap map = null;

    private String airport = null;
    private String carrier = null;
    private String flight = null;


    /*Constants*/
    private static final int REQUEST_PERMISSION = 100;

    public DetailsActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();

        this.airport = args.getString(KEY_AIRPORT);
        this.carrier = args.getString(KEY_CARRIER);
        this.flight = args.getString(KEY_FLIGHT);

        Log.d(TAG, "onCreateView: Airport : " + airport);
        Log.d(TAG, "onCreateView: Carrier : " + carrier);
        Log.d(TAG, "onCreateView: Flight Number : " + flight);

        // Get the flight details asynchronously
        // fetchFlightDetails(airport,carrier,flight);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMyLocationButtonEnabled(true);

                // Check for ACCESS_FINE_LOCATION permission
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSION);
                }
                // Have the permission
                else {
                    doMapStuff();
                }

            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

            String origin = Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude());
            String destination = this.airport.replace(' ','+');

            Call<RouteDirections> call = RetrofitHelper.getGoogleMapsServiceInstance().getDirections(origin,destination,API_KEY);
            call.enqueue(new Callback<RouteDirections>() {
                @Override
                public void onResponse(Call<RouteDirections> call, Response<RouteDirections> response) {
                    Log.d(TAG, "onResponse: Response received");
                    Log.d(TAG, "onResponse: " + call.request().url().toString());
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    Toast.makeText(getActivity(), "Response received", Toast.LENGTH_SHORT).show();
                    RouteDirections directions = response.body();

                    Log.d(TAG, "onResponse: " + directions.getRoutes().size());

                }

                @Override
                public void onFailure(Call<RouteDirections> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    public Location getCurrentLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
        return location;
    }

}
