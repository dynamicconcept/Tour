package com.example.jasim.tour.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.example.jasim.tour.R;
import com.example.jasim.tour.database.EventsDbSource;
import com.example.jasim.tour.model.Events;

import java.util.Calendar;


public class EventAddActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    com.example.jasim.tour.databinding.ActivityEventAddBinding binding;
    SharedPreferences sharedPreferences;
    boolean showFullScreen;
    Calendar calendar;
    int year, month, day, startY, startM, startD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_add);
        setSupportActionBar(binding.toolbar.toolBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Add New Tour");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting settings from SharedPreferences
        sharedPreferences = getSharedPreferences("appsData", MODE_PRIVATE);
        showFullScreen = sharedPreferences.getBoolean("showFullScreen", false);
        if (showFullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.eventStartDate.setOnTouchListener(this);
        binding.eventEndDate.setOnTouchListener(this);
        binding.cancelBtn.setOnClickListener(this);
        binding.submitBtn.setOnClickListener(this);
        binding.eventBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    addDate("start");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public void addDate(final String action) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                if (action.equals("start")) {
                    if (y == year && m >= month && d >= day || y > year) {
                        startY = y;
                        startM = m;
                        startD = d;
                        String dates = startD + "/" + (startM + 1) + "/" + startY;
                        binding.eventStartDate.setError(null);
                        binding.eventStartDate.setText(dates);
                    } else {
                        if (!binding.eventStartDate.getText().toString().equals(""))
                            binding.eventStartDate.setText("");
                        binding.eventStartDate.setError("You choose wrong date");
                    }
                } else {
                    if (y == startY && m >= startM && d >= startD || y > startY) {
                        String dates = d + "/" + (m + 1) + "/" + y;
                        binding.eventEndDate.setError(null);
                        binding.eventEndDate.setText(dates);
                    } else {
                        if (!binding.eventEndDate.getText().toString().equals(""))
                            binding.eventEndDate.setText("");
                        binding.eventEndDate.setError("Date past from start");
                    }
                }
            }
        }, year, month, day);
        datePickerDialog.setTitle("Select Your Date");
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cancelBtn:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.submitBtn:
                String name = binding.eventName.getText().toString().trim();
                String location = binding.eventLocation.getText().toString().trim();
                String stringBudget = binding.eventBudget.getText().toString().trim();
                int budget = 0;
                if (!stringBudget.equals(""))
                    budget = Integer.valueOf(stringBudget);
                String startDate = binding.eventStartDate.getText().toString().trim();
                String endDate = binding.eventEndDate.getText().toString().trim();
                if (name.equals("") || location.equals("") || startDate.equals("") || endDate.equals("")) {
                    if (endDate.equals("")) {
                        binding.eventEndDate.setError("End date required");
                        binding.eventEndDate.requestFocus();
                    }
                    if (startDate.equals("")) {
                        binding.eventStartDate.setError("Start date required");
                        binding.eventStartDate.requestFocus();
                    }
                    if (location.equals("")) {
                        binding.eventLocation.setError("Location required");
                        binding.eventLocation.requestFocus();
                    }
                    if (name.equals("")) {
                        binding.eventName.setError("Event Name required");
                        binding.eventName.requestFocus();
                    }
                } else {
                    long eventUser = getIntent().getLongExtra("userId", 0);
                    if (eventUser != 0) {
                        Events events = new Events();
                        events.setEventUser(eventUser);
                        events.setEventName(name);
                        events.setEventLocation(location);
                        events.setBudget(budget);
                        events.setEventStartDate(startDate);
                        events.setEventEndDate(endDate);
                        EventsDbSource eventDbSource = new EventsDbSource(this);
                        events=eventDbSource.addEvent(events);
                        if (events!=null)
                        setResult(RESULT_OK, new Intent()
                                .putExtra("newEvent", events));

                    }
                    finish();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();
        switch (id) {
            case R.id.eventStartDate:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) addDate("start");
                break;

            case R.id.eventEndDate:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) addDate("end");
                break;

            default:
                break;
        }
        return false;
    }
}
