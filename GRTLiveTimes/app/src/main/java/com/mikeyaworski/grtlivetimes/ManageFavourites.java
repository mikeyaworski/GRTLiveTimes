package com.mikeyaworski.grtlivetimes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ManageFavourites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_favourites);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        populateFavourites();

    }

    public void populateFavourites() {

        // get the favourites data from the database
        final FavouritesDBHelper mDbHelper = new FavouritesDBHelper(this);
        final Cursor cursor = mDbHelper.getData();

        // populate the ListView
        FavouritesCursorAdapter favouritesCursorAdapter = new FavouritesCursorAdapter(this, cursor);
        ListView favouritesList = (ListView)findViewById(R.id.favourites_list);
        favouritesList.setAdapter(favouritesCursorAdapter);

        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // the favourite that was clicked has the following fields
                final TextView stopNumberTV = (TextView)view.findViewById(R.id.stopNumber);
                final TextView routeNumberTV = (TextView)view.findViewById(R.id.routeNumber);
                final TextView descriptionTV = (TextView)view.findViewById(R.id.description);
                final int view_id = (Integer)view.getTag(R.id.ID_TAG);

                // create a dialog giving them the option to edit or delete the favourite
                new AlertDialog.Builder(ManageFavourites.this)
                    .setMessage("Edit or Delete?")
                    .setCancelable(true)
                    .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dial, int which) {

                            // start a new dialog activity to edit the favourite
                            // and send data about the favourite being edited to the activity
                            Intent intent = new Intent(ManageFavourites.this, EditFavourite.class);

                            intent.putExtra("stop", stopNumberTV.getText().toString());
                            intent.putExtra("route", routeNumberTV.getText().toString());
                            intent.putExtra("description", descriptionTV.getText().toString());
                            intent.putExtra("id", String.valueOf(view_id));

                            startActivity(intent);

                            dial.dismiss();
                        }
                    })
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dial, int which) {
                            // delete the favourite from the database and repopulate the favourites list
                            mDbHelper.deleteFavourite(String.valueOf(view_id));
                            populateFavourites();

                            dial.dismiss();
                        }
                    }).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFavourites();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_favourite) {
            // start dialog activity to add a new favourite
            startActivity(new Intent(ManageFavourites.this, AddFavourite.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
