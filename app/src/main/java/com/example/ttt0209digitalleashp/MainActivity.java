package com.example.ttt0209digitalleashp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ////////
    //HOLLER GPS
    //
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location locLocation;
    //
    //
    ////////

    ////////
    //HOLLER DATA
    //
    private final String SHARED_KEY = "TTT0209";
    private SharedPreferences sharedPreferences;
    //
    //
    ////////

    private TextView textLatitude;
    private TextView textLongitude;
    private EditText editUsername;
    private EditText editRadius;
    private EditText editAddChild;
    private ListView listChildren;

    private final String KEY_LATITUDE  = "Latitude";
    private final String KEY_LONGITUDE  = "Longitude";
    private final String KEY_USERNAME  = "Username";
    private final String KEY_RADIUS  = "Radius";
    private final String KEY_CHILDLIST  = "ChildList";

    private double dblDistance;

    //uses %s variables when referenced
    private final String RemoteUrl = "https://turntotech.firebaseio.com/digitalleash/users/%s/%s.json";

    //to interact with ListView
    private List<Person> childList = new ArrayList<>();
    private PersonListAdapter adapter;


//    //another way to quickly implement a class
//    private AsyncTask async = new AsyncTask<String, Void, Void>() {
//        @Override
//        protected Void doInBackground(String... strings) {
//            return null;
//        }
//    };

    ////////
    ////////
    ////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        editUsername  = findViewById(R.id.editUsername);
        editRadius = findViewById(R.id.editRadius);
        editAddChild = findViewById(R.id.editAddChild);

        ////////
        ////////
        ////////

        ////////
        //ListView interaction
        // List -> adapter -> ListView
        //
        //set the adapter to this ArrayList
        adapter = new PersonListAdapter(this, childList);
        listChildren = (ListView) findViewById(R.id.listChildren);

        //set list into view
        listChildren.setAdapter(adapter);
        //
        //
        ////////

        //for long click-and-hold on item
        listChildren.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //get child info
                Person p = (Person) listChildren.getItemAtPosition(position);
                String strChildName = p.getFullName();

                //create URL
                String strURL = String.format(RemoteUrl, editUsername.getText() ,strChildName);

                //pull data from internet
                new HttpGetAsyncTask().execute(strURL);
                //navigation done in PostExecute

                //option to use quick class creation way
                //async.execute("");

                return false;

            }
        });

        ////////
        //HOLLER DATA
        //
        //set in onCreate
        sharedPreferences = getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
        //
        //
        ////////


        ////////
        //set saved data, if exists
        //
        //username
        editUsername.setText(sharedPreferences.getString(KEY_USERNAME,null));
        //radius
        editRadius.setText(sharedPreferences.getString(KEY_RADIUS,null));
        //latitude
        textLatitude.setText(sharedPreferences.getString(KEY_LATITUDE,null));
        //longitude
        textLongitude.setText(sharedPreferences.getString(KEY_LONGITUDE,null));

        //get string set
        Set<String> set = sharedPreferences.getStringSet(KEY_CHILDLIST, null);
        int i = 0;
        Person p;

        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            p = new Person(iterator.next().toString());
            childList.add(p);
        }

        //Toast.makeText(MainActivity.this, "Holler", Toast.LENGTH_LONG).show();
        //
        //
        ////////


        ////////
        //HOLLER GPS
        //
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //set accessible variable to new location
                locLocation = location;
                //STOP LOCATIONMANAGER FROM RECORDING, I.E. STOPS EVENTS
                //locationManager.removeUpdates(locationListener);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


        // Check if location permission exists.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                // If permission does not exists, request for it.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
        else {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        //
        //
        ////////

    }



    public void buttonClickedUpdate(View view) {

        //set updated location
        if(locLocation==null){
            Toast.makeText(MainActivity.this, "Location is null", Toast.LENGTH_LONG).show();
        }
        else {
            textLatitude.setText(String.format("%.2f",locLocation.getLatitude()));
            textLongitude.setText(String.format("%.2f",locLocation.getLongitude()));

            Toast.makeText(MainActivity.this, "Location Set", Toast.LENGTH_LONG).show();

        }
    }


    public String[] getArrStringFromListView(ListView lv) {
        //converts elements of ListView into String

        Person p;
        int intNumberOfItems = lv.getAdapter().getCount();
        int i = 0;
        String[] s = new String[intNumberOfItems];

        while (i < lv.getAdapter().getCount()) {
            p = (Person) lv.getItemAtPosition(i);
            s[i] = p.getFullName(); //lv.getAdapter().getItem(i).toString();
            i++;
        }

        return s;
    }

    public void buttonClickedSaveSettings(View view) {

        //set string array
        String[] s = getArrStringFromListView(listChildren);
        //create set from string array
        Set<String> set = new HashSet<>(Arrays.asList(s));

        //save data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editUsername.getText().toString();
        editor.putString(KEY_USERNAME, editUsername.getText().toString());
        editor.putString(KEY_RADIUS, editRadius.getText().toString());
        editor.putString(KEY_LATITUDE, textLatitude.getText().toString());
        editor.putString(KEY_LONGITUDE, textLongitude.getText().toString());
        //save set of strings
        editor.putStringSet(KEY_CHILDLIST, set);

        editor.commit();

        Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_LONG).show();

    }

    public void buttonClickedAddChild(View view) {

        //adds child to list
        Person p = new Person(editAddChild.getText().toString());
        childList.add(p);
        adapter.notifyDataSetChanged();

        Toast.makeText(MainActivity.this, "Child added", Toast.LENGTH_LONG).show();

    }


    public void navigateToCorrect() {
    //need to wait for PostExecute, else data is blank
        String strImageUrl;
        //set image location based on result
        if (dblDistance < Double.valueOf(editRadius.getText().toString())) {
            //success
            strImageUrl = "https://previews.123rf.com/images/thomaspajot/thomaspajot1202/thomaspajot120200008/12494605-thumb-good.jpg";
        }
        else {
            //fail
            strImageUrl = "https://cdn.pixabay.com/photo/2013/07/13/10/32/bad-157437_960_720.png";
        }

        //navigation
        Intent intent = new Intent(MainActivity.this, ChildCheckActivity.class);
        //pass variables to next activity
        intent.putExtra("EXTRA_URL", strImageUrl);
        intent.putExtra("EXTRA_DISTANCE", dblDistance);
        intent.putExtra("EXTRA_RADIUS", Double.valueOf(editRadius.getText().toString()));
        //intent.putExtra("EXTRA_DISTANCE", ""+dblDistance);
        startActivity(intent);

    }


    ////////
    //HOLLER INTERNET
    //
    private Double downloadData(String strUrlString) {

        final String TAG = "hollerrr:";

        try {
            URL url = new URL(strUrlString);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");

            //check this after connecting to make sure it worked
            int intStatusCode = urlConnection.getResponseCode();

            //get input stream
            InputStream is = urlConnection.getInputStream();

            //convert to JSON
            String s = Utilities.inputStreamToString(is);
            JSONObject jsonObject = new JSONObject(s);

            //set lat & lon
            double dblChildLatitude = Double.valueOf(jsonObject.getString("latitude"));
            double dblChildLongitude = Double.valueOf(jsonObject.getString("longitude"));

            //convert to location
            Location childLocation = new Location("child");
            childLocation.setLatitude(dblChildLatitude);
            childLocation.setLongitude(dblChildLongitude);

            //find distance
            dblDistance = locLocation.distanceTo(childLocation);

            //log
            Log.d(TAG, jsonObject.toString());
            Log.d(TAG +" d:", "distance: " + dblDistance);

            return dblDistance;

        }
        catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }

    }

    private class HttpGetAsyncTask extends AsyncTask<String, Void, Double> {

        ////////
        //HOLLER
        // to pass data out of AsyncTask, must call a function from one of
        // the methods below
        //
        ////////

        // This is a constructor that allows you to pass in the JSON body
        public HttpGetAsyncTask() {
            super();
        }

        @Override
        protected Double doInBackground(String... params) {

            double dblResult = downloadData(params[0]);
            return dblResult;
        }

        @Override
        protected void onPostExecute(Double dblResult) {

            super.onPostExecute(dblResult);

            //if(dblResult != null && dblResult < Double.valueOf(editRadius.getText().toString())) {
            if(dblResult != -1) {
                //show result
                navigateToCorrect();
            }
            else {
                //warn user that there was an error
                Toast.makeText(MainActivity.this, "Error downloading data!\nPlease check Usernames and try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //
    //
    ////////

    ////////
    //HOLLER GPS
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Location Permission Not Granted, Punk!", Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(MainActivity.this, "Blah Blah Toast", Toast.LENGTH_LONG).show(); //holler
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
        }
    }
    //
    //
    ////////



}
