package com.deeptimay.yuluvenues.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptimay.yuluvenues.R;
import com.deeptimay.yuluvenues.activity.MapsActivity;
import com.deeptimay.yuluvenues.pojos.FoursquareResults;
import com.deeptimay.yuluvenues.pojos.FoursquareVenue;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PlacePickerAdapter extends RecyclerView.Adapter<PlacePickerAdapter.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of results from the Foursquare API
    private List<FoursquareVenue> results;

    public PlacePickerAdapter(Context context, List<FoursquareVenue> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public PlacePickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_picker, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Sets the proper rating colour, referenced from the Foursquare Brand Guide
        double ratingRaw = results.get(position).rating;
        if (ratingRaw >= 9.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQKale));
        } else if (ratingRaw >= 8.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQGuacamole));
        } else if (ratingRaw >= 7.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQLime));
        } else if (ratingRaw >= 6.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQBanana));
        } else if (ratingRaw >= 5.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQOrange));
        } else if (ratingRaw >= 4.0) {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQMacCheese));
        } else {
            holder.rating.setBackgroundColor(ContextCompat.getColor(context, R.color.FSQStrawberry));
        }

        // Sets each view with the appropriate venue details
        holder.name.setText(results.get(position).name);
        holder.address.setText(results.get(position).location.address);
        holder.rating.setText(Double.toString(ratingRaw));
        holder.distance.setText(results.get(position).location.distance + "m");

        // Stores additional venue details for the map view
        holder.id = results.get(position).id;
        holder.latitude = results.get(position).location.lat;
        holder.longitude = results.get(position).location.lng;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The venue fields to display
        TextView name;
        TextView address;
        TextView rating;
        TextView distance;
        String id;
        double latitude;
        double longitude;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);

            // Gets the appropriate view for each venue detail
            name = v.findViewById(R.id.placePickerItemName);
            address = v.findViewById(R.id.placePickerItemAddress);
            rating = v.findViewById(R.id.placePickerItemRating);
            distance = v.findViewById(R.id.placePickerItemDistance);
        }

        @Override
        public void onClick(View v) {

            // Creates an intent to direct the user to a map view
            Context context = name.getContext();
            Intent i = new Intent(context, MapsActivity.class);

            // Passes the crucial venue details onto the map view
            i.putExtra("name", name.getText());
            i.putExtra("ID", id);
            i.putExtra("latitude", latitude);
            i.putExtra("longitude", longitude);

            // Transitions to the map view.
            context.startActivity(i);
        }
    }
}