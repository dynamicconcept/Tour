package com.example.jasim.tour.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import com.example.jasim.tour.R;
import com.example.jasim.tour.databinding.EventListViewBinding;
import com.example.jasim.tour.model.Events;

public class EventsAdapter extends ArrayAdapter<Events> {

    Context context;
    ArrayList<Events> events = new ArrayList<>();

    public EventsAdapter(Context context, ArrayList<Events> events) {
        super(context, R.layout.event_list_view, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Events getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).getEventID();
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final EventListViewBinding binding;
        if (view == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.event_list_view, parent, false);

        } else {
            binding = DataBindingUtil.bind(view);
        }
        String eventName="Tour Name: " + events.get(position).getEventName();
        binding.eventName.setText(eventName);
        String eventLocation="Location: " + events.get(position).getEventLocation();
        binding.eventLocation.setText(eventLocation);
        String startDate="Start: " + events.get(position).getEventStartDate();
        binding.startDate.setText(startDate);
        String endDate="End: " + events.get(position).getEventEndDate();
        binding.endDate.setText(endDate);
        return binding.getRoot();
    }
}
