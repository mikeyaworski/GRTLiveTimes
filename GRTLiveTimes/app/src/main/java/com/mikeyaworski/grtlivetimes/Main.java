package com.mikeyaworski.grtlivetimes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.DecimalFormat;

public class Main extends AppCompatActivity {

    private EditText txtStop, txtRoute;
    private TextView display;

    private static final String GRT_API_URL = "http://nwoodthorpe.com/grt/V2/livetime.php?stop="; // https://github.com/nwoodthorpe/GRTUnofficialLiveAPI
    private static final String NO_DATA_FOUND_ERROR_MESSAGE = "No data loaded. Stop was not found or busses are not running anymore.";
    private static final String MALFORMED_INPUT_ERROR_MESSAGE = "Invalid input.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        txtStop = (EditText)findViewById(R.id.txtStop);
        txtRoute = (EditText)findViewById(R.id.txtRoute);
        display = (TextView)findViewById(R.id.display);

        populateFavourites();
    }

    public void populateFavourites() {

        // get the favourites data from the database
        FavouritesDBHelper mDbHelper = new FavouritesDBHelper(this);
        Cursor cursor = mDbHelper.getData();

        // populate the ListView
        FavouritesCursorAdapter favouritesCursorAdapter = new FavouritesCursorAdapter(this, cursor);
        ListView favouritesList = (ListView)findViewById(R.id.favourites_list);
        favouritesList.setAdapter(favouritesCursorAdapter);

        // listen for when a favourite in the list is clicked
        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // refresh the live times feed for any favourite chosen in the list
                TextView stopNumberTV = (TextView)view.findViewById(R.id.stopNumber);
                TextView routeNumberTV = (TextView)view.findViewById(R.id.routeNumber);
                txtStop.setText(stopNumberTV.getText().toString());
                txtRoute.setText(routeNumberTV.getText().toString());
                refresh();
            }
        });
    }

    public void refresh(View v) {
        if (isOnline()) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE); // start loading animation
            new ShowTimes(txtStop.getText().toString(), txtRoute.getText().toString()).execute(); // refresh the live times feed
        } else {
            Toast.makeText(getApplicationContext(), "You're not connected to the internet!", Toast.LENGTH_SHORT).show();
        }
    }
    public void refresh() {
        refresh(null);
    }

    // private class that runs an asynchronous thread to fetch data from the API
    // then displays the results to the main UI after the thread is completed
    private class ShowTimes extends AsyncTask<String, Void, String> {

        private String stop;
        private String routePref;

        public ShowTimes(String stop, String routePref) {
            this.stop = stop;
            this.routePref = routePref;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(GRT_API_URL + stop);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                if (httpURLConnection.getResponseCode() == 404) {
                    // the response code is 404 when there was no data that could be loaded
                    // so return a JSON string that can be parsed into its error message in onPostExecute
                    return "{\"ERROR_MESSAGE\": \"" + NO_DATA_FOUND_ERROR_MESSAGE + "\"}";
                } else if (httpURLConnection.getResponseCode() == 400) {
                    // the response code is 400 when there is malformed input (most likely the stop number)
                    // so return a JSON string that can be parsed into its error message in onPostExecute
                    return "{\"ERROR_MESSAGE\": \"" + MALFORMED_INPUT_ERROR_MESSAGE + "\"}";
                } else {
                    // should be good to parse the url data
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line + "\n");
                    }

                    in.close();
                    httpURLConnection.disconnect();

                    return result.toString();
                }

            } catch(Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            // parse JSON data
            try {
                JSONObject jObject = new JSONObject(result);

                if (jObject.has("data")) { // there is stop data (in particular, there is an array of data)

                    // will be printed to the display TextView after all data is parsed
                    StringBuffer output = new StringBuffer();

                    // iterate through each route arriving at the stop
                    JSONArray jArray = jObject.getJSONArray("data");
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject route = jArray.getJSONObject(i);

                        // only display preferred route (if one was inputted)
                        if (routePref.equals("") || routePref.equals(route.getString("routeId"))) {

                            // spacing if not the first output
                            if (output.length() != 0) {
                                output.append("\n\n");
                            }

                            output.append(route.getString("name")); // output the route name

                            // iterate through each bus from this route coming to the stop
                            // e.g. if the route is 12, then there might be different 12 buses coming every 30 minutes
                            JSONArray buses = route.getJSONArray("stopDetails");
                            for (int j = 0; j < buses.length(); j++) {
                                // output the live time that this bus will come to the stop
                                int departure = buses.getJSONObject(j).getInt("departure");
                                String time = getTimeFromDepartureInt(departure);
                                output.append("\n" + time);
                            }
                        }
                    }

                    display.setText(output.toString());

                    // if the output is blank and a route was inputted, then that route must not come to this stop
                    if (output.toString().equals("") && !routePref.equals("")) {
                        display.setText("That route does not depart from this stop!");
                    }
                } else {
                    // if there's no data object, there must be an error code
                    display.setText(jObject.getString("ERROR_MESSAGE"));
                }

            } catch (JSONException je) {
                display.setText("Something went wrong.");
                je.printStackTrace();
            }

            findViewById(R.id.loadingPanel).setVisibility(View.GONE); // remove loading animation
        }

        // returns a formatted string of the time that the bus will arrive
        // parameter is number of seconds since midnight of this day
        private String getTimeFromDepartureInt(int departure) {
            int hour = departure / 3600;
            int min = (int)Math.round((departure - hour * 3600) / 60.0);

            if (min == 60) {
                min = 0;
                hour++;
            }

            String amOrPm = "am";
            if (hour > 11) {
                if (hour != 24) amOrPm = "pm";
                if (hour > 12) hour -= 12;
            }
            if (hour == 0) hour = 12;

            DecimalFormat df = new DecimalFormat("00");

            return String.valueOf(hour) + ":" + df.format(min) + " " + amOrPm;
        }

        @Override protected void onPreExecute() { }
        @Override protected void onProgressUpdate(Void... values) { }
    }

    public boolean isOnline() {
        final ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true; // online
        } else {
            return false; // offline
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFavourites();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.manage_favourites) {
            Intent intent = new Intent(Main.this, ManageFavourites.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
