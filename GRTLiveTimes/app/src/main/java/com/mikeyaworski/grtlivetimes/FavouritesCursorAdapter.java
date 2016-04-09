package com.mikeyaworski.grtlivetimes;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FavouritesCursorAdapter extends CursorAdapter {

    public FavouritesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0); // 0 to set no flags
    }

    // used to inflate a new view and return it (don't bind any data to the view at this point)
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.favourite_item, parent, false);
    }

    // used to bind all data to a given view (set the values of the TextViews)
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // set the view to have a tag that is the ID of the row in the database
        // this tag will be used to edit and delete favourites (need ID to edit/delete a particular row)
        view.setTag(R.id.ID_TAG, cursor.getInt(cursor.getColumnIndexOrThrow(FavouritesDBHelper.ID_FIELD)));

        // fields to populate in inflated template
        TextView stopNumberTV = (TextView)view.findViewById(R.id.stopNumber);
        TextView routeNumberTV = (TextView)view.findViewById(R.id.routeNumber);
        TextView descriptionTV = (TextView)view.findViewById(R.id.description);

        // extract properties from cursor
        String stopNumber = cursor.getString(cursor.getColumnIndexOrThrow(FavouritesDBHelper.STOP_NUMBER_FIELD));
        String routeNumber = cursor.getString(cursor.getColumnIndexOrThrow(FavouritesDBHelper.ROUTE_NUMBER_FIELD));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(FavouritesDBHelper.DESCRIPTION_FIELD));

        // populate fields with extracted properties
        stopNumberTV.setText(stopNumber);
        routeNumberTV.setText(routeNumber);
        descriptionTV.setText(description);
    }
}
