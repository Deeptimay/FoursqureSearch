package com.deeptimay.yuluvenues.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptimay.yuluvenues.R;
import com.deeptimay.yuluvenues.adapters.PlacePickerAdapter;
import com.deeptimay.yuluvenues.networkCalls.FoursquareService;
import com.deeptimay.yuluvenues.pojos.FoursquareJSON;
import com.deeptimay.yuluvenues.pojos.FoursquareResponse;
import com.deeptimay.yuluvenues.pojos.FoursquareVenue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacePickerActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // The client object for connecting to the Google API
    private GoogleApiClient mGoogleApiClient;

    // The TextView for displaying the current location
    private TextView snapToPlace;

    // The RecyclerView and associated objects for displaying the nearby coffee spots
    private RecyclerView placePicker;
    private LinearLayoutManager placePickerManager;
    private RecyclerView.Adapter placePickerAdapter;

    // The base URL for the Foursquare API
    private String foursquareBaseURL = "https://api.foursquare.com/v2/";
    private String sharedPref = "yuluVenues";
    private String searchedResult = "yuluSearches";

    // The client ID and client secret for authenticating with the Foursquare API
    private String foursquareClientID;
    private String foursquareClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // The visible TextView and RecyclerView objects
        snapToPlace = findViewById(R.id.snapToPlace);
        placePicker = findViewById(R.id.coffeeList);

        // Sets the dimensions, LayoutManager, and dividers for the RecyclerView
        placePicker.setHasFixedSize(true);
        placePickerManager = new LinearLayoutManager(this);
        placePicker.setLayoutManager(placePickerManager);
        placePicker.addItemDecoration(new DividerItemDecoration(placePicker.getContext(), placePickerManager.getOrientation()));

        try {

            SharedPreferences prefs = getSharedPreferences(sharedPref, MODE_PRIVATE);
            String name = prefs.getString(searchedResult, "null");
            if (!name.equalsIgnoreCase("null")) {
                Gson gson = new Gson();
                FoursquareResponse results = gson.fromJson(name, FoursquareResponse.class);
                List<FoursquareVenue> frs = results.venues;
                FoursquareVenue fv = frs.get(0);

                // Notifies the user of their current location
                snapToPlace.setText("You're at " + fv.name + ". Here's some nearby places.");

                placePickerAdapter = new PlacePickerAdapter(getApplicationContext(), frs);
                placePicker.setAdapter(placePickerAdapter);
            }
        } catch (Exception e) { }

        // Creates a connection to the Google API for location services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Gets the stored Foursquare API client ID and client secret from XML
        foursquareClientID = getResources().getString(R.string.foursquare_client_id);
        foursquareClientSecret = getResources().getString(R.string.foursquare_client_secret);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // Checks for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Makes a Google API request for the user's last known location
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                // The user's current latitude, longitude, and location accuracy
                String userLL = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                double userLLAcc = mLastLocation.getAccuracy();

                // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(foursquareBaseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                FoursquareService foursquare = retrofit.create(FoursquareService.class);

                // Calls the Foursquare API to snap the user's location to a Foursquare venue
                Call<FoursquareJSON> stpCall = foursquare.snapToPlace(
                        foursquareClientID,
                        foursquareClientSecret,
                        userLL,
                        userLLAcc);
                stpCall.enqueue(new Callback<FoursquareJSON>() {
                    @Override
                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                        // Gets the venue object from the JSON response
                        FoursquareJSON fjson = response.body();
                        FoursquareResponse fr = fjson.response;
                        List<FoursquareVenue> frs = fr.venues;
                        FoursquareVenue fv = frs.get(0);

                        // Notifies the user of their current location
                        snapToPlace.setText("You're at " + fv.name + ". Here's some nearby places.");

                        placePickerAdapter = new PlacePickerAdapter(getApplicationContext(), frs);
                        placePicker.setAdapter(placePickerAdapter);
                        placePickerAdapter.notifyDataSetChanged();

                        Gson gson = new Gson();
                        String result = gson.toJson(fr);

                        SharedPreferences sharedpreferences = getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(searchedResult, result);
                        editor.apply();
                    }

                    @Override
                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
//                        finish();
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Can't determine your current location!", Toast.LENGTH_LONG).show();
//                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reconnects to the Google API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Disconnects from the Google API
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Can't connect to Google's servers!", Toast.LENGTH_LONG).show();
//        finish();
    }
}