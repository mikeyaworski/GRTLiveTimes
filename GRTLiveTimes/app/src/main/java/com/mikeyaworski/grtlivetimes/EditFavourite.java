package com.mikeyaworski.grtlivetimes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditFavourite extends AppCompatActivity {

    private EditText txtStop, txtRoute, txtDescription;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_favourite);

        txtStop = (EditText)findViewById(R.id.txtStop);
        txtRoute = (EditText)findViewById(R.id.txtRoute);
        txtDescription = (EditText)findViewById(R.id.txtDescription);

        // get data about this favourite from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtStop.setText(extras.getString("stop"));
            txtRoute.setText(extras.getString("route"));
            txtDescription.setText(extras.getString("description"));
            id = extras.getString("id");
        }
    }

    public void submit(View v) {
        // update the database if they entered valid data (at least a stop number)
        if (txtStop.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a stop number.", Toast.LENGTH_SHORT).show();
        } else {
            // update the database and close the activity
            FavouritesDBHelper mDbHelper = new FavouritesDBHelper(this);
            mDbHelper.updateFavourite(id, txtStop.getText().toString(), txtRoute.getText().toString(), txtDescription.getText().toString());
            finish();
        }
    }

    public void cancel(View v) {
        finish();
    }
}
