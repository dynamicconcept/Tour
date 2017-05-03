package com.example.jasim.tour.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.jasim.tour.R;
import com.example.jasim.tour.databinding.EventDetailsListViewBinding;
import com.example.jasim.tour.model.EventDetails;


public class EventDetailsAdapter extends ArrayAdapter<EventDetails>{
    Context context;
    ArrayList<EventDetails> eventDetails = new ArrayList<>();

    public EventDetailsAdapter(Context context, ArrayList<EventDetails> eventDetails) {
        super(context, R.layout.event_details_list_view, eventDetails);
        this.context = context;
        this.eventDetails = eventDetails;
    }

    @Override
    public int getCount() {
        return eventDetails.size();
    }

    @Override
    public EventDetails getItem(int position) {
        return eventDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return eventDetails.get(position).getId();
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final EventDetailsListViewBinding binding;
        if (view == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.event_details_list_view, parent, false);
            Log.d("Test", "On create holder call");

        } else {
            binding = DataBindingUtil.bind(view);
        }
        Log.d("Test", "On create holder call "+position);
        binding.tittleTxt.setText(eventDetails.get(position).getDetails());
        binding.timeText.setText(eventDetails.get(position).getAddingTime());
        binding.used.setText(String.valueOf(eventDetails.get(position).getBudgetUsed()));
        String picture = eventDetails.get(position).getPicture();
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(picture.split("@@")));
        ImageAdapter imageAdapter = new ImageAdapter(context, arrayList);
        binding.gridView.setAdapter(imageAdapter);
        binding.gridView.setVerticalScrollBarEnabled(false);
    /*    binding.gridView.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               activity.hideShow(motionEvent.getAction());
                return false;
            }
        });*/
        return binding.getRoot();
    }
}
