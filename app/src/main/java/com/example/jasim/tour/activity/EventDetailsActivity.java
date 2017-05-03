package com.example.jasim.tour.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.jasim.tour.R;
import com.example.jasim.tour.adapter.EventDetailsAdapter;
import com.example.jasim.tour.database.EventDetailsDbSource;
import com.example.jasim.tour.databinding.ActivityEventDetailsBinding;
import com.example.jasim.tour.model.EventDetails;
import com.example.jasim.tour.model.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



public class EventDetailsActivity extends AppCompatActivity {

    ActivityEventDetailsBinding binding;
    EventDetailsDbSource dbSource;
    ArrayList<EventDetails> eventDetails;
    EventDetailsAdapter adapter;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean hideToolbar;
    boolean showFullScreen;
    boolean expired;
    Date date;
    Events event;
    int eventUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);
        sharedPreferences = getSharedPreferences("appsData", MODE_PRIVATE);
        hideToolbar = sharedPreferences.getBoolean("hideToolbar", false);
        showFullScreen = sharedPreferences.getBoolean("showFullScreen", false);
        event = (Events) getIntent().getSerializableExtra("event");
        setSupportActionBar(binding.toolbar.toolBar);
        String title = event.getEventName().toUpperCase() + " to "
                + event.getEventLocation().toUpperCase();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.toolBar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //configure settings
        if (hideToolbar) getSupportActionBar().hide();
        if (showFullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentTime = format.format(new Date());
        Date currentDate=null;
        try {
            date=format.parse(event.getEventEndDate());
            currentDate=format.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentDate.after(date) && currentDate!=null) {
            expired = true;
            binding.eventAddBtn.hide();
        }

//        LayoutInflater inflater = getLayoutInflater();
//        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, binding.eventListView, false);
//        binding.eventListView.addHeaderView(header, null, false);
        dbSource = new EventDetailsDbSource(this);
        eventDetails = dbSource.getEventsDetails(event.getEventID());
//        eventDetails=new ArrayList<>();
//        for (int i=0; i<100; i++){
//            EventDetails event=new EventDetails(1, "dhs0", "fff", "ffdd",02);
//            eventDetails.add(event);
//        }

        if (!eventDetails.isEmpty()) {
            eventDetailsInitial();
            getBudget();
        }
        binding.eventAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(EventDetailsActivity.this,
                        EventsDetailsAddActivity.class).putExtra("eventId",
                        event.getEventID()), 0);
            }
        });
//        binding.eventListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                hideShow(motionEvent.getAction());
//                return false;
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (showFullScreen) menu.findItem(R.id.fullScreen).setTitle(R.string.hideFullScreen);
        else menu.findItem(R.id.fullScreen).setTitle(R.string.showFullScreen);
        if (hideToolbar) menu.findItem(R.id.hideToolbar).setTitle(R.string.showToolbar);
        else menu.findItem(R.id.hideToolbar).setTitle(R.string.hideToolbar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        editor = sharedPreferences.edit();

        if (id == R.id.hideToolbar) {
            if (hideToolbar) {
                hideToolbar = false;
                editor.putBoolean("hideToolbar", false);
                editor.apply();
                if (getSupportActionBar() != null)
                getSupportActionBar().show();
            } else {
                hideToolbar = true;
                editor.putBoolean("hideToolbar", true);
                editor.apply();
                if (getSupportActionBar() != null)
                getSupportActionBar().hide();
            }
            return true;
        }
        if (id == R.id.fullScreen) {
            if (showFullScreen) {
                showFullScreen = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                editor.putBoolean("showFullScreen", false);
                editor.apply();
            } else {
                showFullScreen = true;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                editor.putBoolean("showFullScreen", true);
                editor.apply();
            }
            return true;
        }
        editor.apply();
        return super.onOptionsItemSelected(item);
    }

    public void hideShow(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
//            getSupportActionBar().hide();
            binding.availableBudget.setVisibility(View.GONE);
            binding.eventAddBtn.hide();
        }
        if (action == MotionEvent.ACTION_UP) {
//            getSupportActionBar().show();
            binding.availableBudget.setVisibility(View.VISIBLE);
            if (!expired) binding.eventAddBtn.show();
        }

    }

    private void getBudget() {
        eventUsed = event.getBudget() - dbSource.getEventTotalUsed(event.getEventID());
        String availableBudget="Available " + String.valueOf(eventUsed);
        binding.availableBudget.setVisibility(View.VISIBLE);
        binding.availableBudget.setText(availableBudget);
        if (eventUsed < 1000 && !expired)
            binding.availableBudget.setBackgroundColor(Color.RED);
        if (eventUsed < 500 && !expired) {
            AlertDialog.Builder dialog;
            //checking android version for lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                dialog = new AlertDialog.Builder(this);
            }
            dialog.setMessage("You are going to out of money!!!").setCancelable(false).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                eventDetails.add((EventDetails)
                        data.getSerializableExtra("newEventDetails"));
                if (adapter == null) eventDetailsInitial();
                else adapter.notifyDataSetChanged();
                getBudget();
            }
        } else
            Toast.makeText(EventDetailsActivity.this,
                    R.string.result_cancel, Toast.LENGTH_SHORT).show();
    }

    private void eventDetailsInitial() {
        binding.noEventText.setVisibility(View.GONE);
        adapter = new EventDetailsAdapter(EventDetailsActivity.this,
                eventDetails);
        binding.eventListView.setAdapter(adapter);
    }

}
