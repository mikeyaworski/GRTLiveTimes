package com.mikeyaworski.grtlivetimes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

// This class isn't actually used, but could be used as an alternative to FavouritesCursorAdapter
public class FavouritesListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemStopNumber;
    private final ArrayList<String> itemRouteNumber;
    private final ArrayList<String> itemDescription;

    public FavouritesListAdapter(Activity context, ArrayList<String> itemStopNumber, ArrayList<String> itemRouteNumber, ArrayList<String> itemDescription) {
        super(context, R.layout.favourite_item);

        this.context = context;

        this.itemStopNumber = itemStopNumber;
        this.itemRouteNumber = itemRouteNumber;
        this.itemDescription = itemDescription;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.favourite_item, null, true);

        TextView stopNumber = (TextView)rowView.findViewById(R.id.stopNumber);
        TextView routeNumber = (TextView)rowView.findViewById(R.id.routeNumber);
        TextView description = (TextView)rowView.findViewById(R.id.description);

        stopNumber.setText(this.itemStopNumber.get(position));
        routeNumber.setText(this.itemRouteNumber.get(position));
        description.setText(this.itemDescription.get(position));

        return rowView;
    }
}

