package com.example.team4_taskapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class calendarFragment extends Fragment{

    // Used to access the user's table holding their tasks.
    private String username = "example@email.com";
    // Array of user's tasks.
    private ArrayList<String> holdTasks;

    private boolean testing_something = true;
    private CalendarView cal;
    private TextView calTF;
    private String s =  "Default Text";

    // Use ConnectionClass to make connection with database.
    ConnectionClass connection;

    private String selected_day = "10";
    private String selected_month = "8";
    private String selected_year = "2021";

    private EditText editTextTask;
    private EditText editTextDate;
    private EditText editTextTime;
    private ToggleButton editToggle;

    private String taskText;
    private String dateText;
    private String timeText;

    private CalListViewFragment nestedFrag;
    private FragmentTransaction ft;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // Constructor
    public calendarFragment() {
        // Required empty public constructor
    }

    // Used to create a new instance.
    public static calendarFragment newInstance(String param1, String param2) {
        calendarFragment fragment = new calendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    // Listen for events and display information on the screen.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Hold the calendar fragment as a view object.
        View cal_frag_view = inflater.inflate(R.layout.fragment_calendar, container, false);
        connection = new ConnectionClass();

        // Find the calendarView and listen for when a date is selected (month starts at 0 so add 1).
        cal = cal_frag_view.findViewById(R.id.calendarView);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                s = "Date: " + (month + 1) + "/" + dayOfMonth + "/" + year;
                Log.d("Time", "Date: " + (month + 1) + "/" + dayOfMonth + "/" + year);

                selected_day = String.valueOf(dayOfMonth);
                selected_month = String.valueOf(month + 1);
                selected_year = String.valueOf(year);
                if (testing_something) {
                    holdTasks = new ArrayList<>();

                    String dateTimeFormat = selected_year + "-" + selected_month + "-" + selected_day;
                    connection = new ConnectionClass();
                    populateTaskArr(dateTimeFormat, connection);
                    Log.d("Size of array2: ", String.valueOf(holdTasks.size()));

                    // Populate the array of task items with the user's tasks for the currently selected day.
                    nestedFrag = new CalListViewFragment(holdTasks, username);
                    ft = getChildFragmentManager().beginTransaction();
                    // Replace the contents of the container with the new fragment
                    ft.replace(R.id.frameLayout1_id, nestedFrag);
                    // or ft.add(R.id.your_placeholder, new FooFragment());
                    // Complete the changes added above
                    ft.commit();
                    Log.d("Updating tasks", "in the list view");

                }
            }
        });

        // Find the floating action button and listen for click.
        FloatingActionButton fab2 = (FloatingActionButton) cal_frag_view.findViewById(R.id.addTaskBttn2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Context", "Floating action button clicked");

                // Display an alert dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("New Task");
                // To use a fragment layout on the alert dialog use getView() to provide ViewGroup
                View new_task_view = LayoutInflater.from(getContext()).inflate(R.layout.new_task_layout, (ViewGroup) getView(), false);

                // Find the input TextViews.
                editTextTask = (EditText) new_task_view.findViewById(R.id.edit_new_task);
                editTextDate = (EditText) new_task_view.findViewById(R.id.date_input);
                editTextDate.setText(selected_month + "/" + selected_day + "/" + selected_year);
                editTextTime = (EditText) new_task_view.findViewById(R.id.time_input);
                editToggle = (ToggleButton) new_task_view.findViewById(R.id.toggleBttn);

                // Make the connection to the database.
                connection = new ConnectionClass();


                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(new_task_view);

                // Listen for button clicks.
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Add Button", "was clicked in alert dialog");
                        // dialog.dismiss(); ****** doesn't seem to be needed.

                        // Grab the data the user left in the TextFields.
                        taskText = editTextTask.getText().toString().trim();
                        dateText = editTextDate.getText().toString().trim();
                        timeText = editTextTime.getText().toString().trim();
                        dateText = dateSeparate(dateText);

                        // Call method to take execute sql query to add task to database.
                        int amPM = toggleState(editToggle);
                        String toastMsg = exeSQLQuery(taskText, dateText, timeText, amPM, connection);
                        Toast.makeText(getContext(), ""+ toastMsg, Toast.LENGTH_LONG).show();

                        if (toastMsg.equals("Task Added")) {
                            populateTaskArr(dateText, connection);
                            // Now that the task was added to the database, refresh listview.
                            nestedFrag = new CalListViewFragment(holdTasks, username);
                            ft = getChildFragmentManager().beginTransaction();
                            ft.replace(R.id.frameLayout1_id, nestedFrag);
                            ft.commit();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel(); ****** doesn't seem to be needed.
                    }
                });

                builder.show();
            }
        });

        //holdTasks = new ArrayList<>();

        // Populate the array of task items with the user's tasks for the current day.
//        holdTasks.add("I hope");
//        holdTasks.add("I hope");
//        holdTasks.add("That this");
//        holdTasks.add("Works please");
        String dateTimeFormat = selected_year + "-" + selected_month + "-" + selected_day;
        populateTaskArr(dateTimeFormat, connection);
        Log.d("Size of array1: ", String.valueOf(holdTasks.size()));

        // Begin the transaction
        Log.d("Begin", "the transaction");
        nestedFrag = new CalListViewFragment(holdTasks, username);
        ft = getChildFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.frameLayout1_id, nestedFrag);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();


        //statsFragment my_stats_frag = (statsFragment) getChildFragmentManager().findFragmentById(R.id.stats_frag_id);
        //my_stats_frag.getAView();
        //View stats_frag_view_in_parent = inflater.inflate(R.layout.fragment_stats, container, false);
        //TextView holdTF = (TextView) my_stats_frag.getAView();
        //holdTF.setText("Accessed");



        // Return the view object of the calendar object.
        return cal_frag_view;
    }




    // ************* Helper Methods *********************

    // Method fills an array with tasks from database.


    // Method to separate a date.
    public String dateSeparate(String date) {
        String[] words = date.split("-|/");
        String dateStr;
        if (words[0].length() == 4) {
            dateStr = words[0] + "-" + words[1] +"-" + words[2];
        }
        else {
            dateStr = words[2] + "-" + words[0] +"-" + words[1];
        }
        return dateStr;
    }

    public String populateTaskArr(String date, ConnectionClass connection) {
        holdTasks = new ArrayList<String>();
        String msg = "";
        //
        try {
            Connection con = connection.CONN();

            // If connection null then error happened in connection class.
            if (con == null){
                msg = "Please check your internet connection";
            }
            else{
                // Define query to send to server
                String query = "SELECT * FROM `" + username + "` WHERE date_time BETWEEN '" + date + " 00:00:00' AND '"+ date + " 23:59:59';";
                Log.d("Inside selecting the data from table", query); // For testing purposes
                // Create statement and send query
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // Add each task to array holding tasks.
                while(rs.next()) {
                    holdTasks.add(rs.getString("task"));
                    Log.d("From the database: ", rs.getString("task"));
                }
                msg = "Retrieved day's tasks";
            }
        } catch (Exception e) {
            msg = "Exceptions "+e;
            e.printStackTrace();
        }
            return msg;
}

    // Method formats inputs for sql query.
    public String formatQuery(String task, String date, String time, int dayState) {
        return "INSERT INTO `" + username + "` VALUES ('" + task + "', '" + date + " " + time + "', b'"+ dayState +"')";
    }

    // Check state of toggle button.
    public int toggleState(ToggleButton tBttn) {
        final int[] tBttnState = new int[1];
        tBttn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tBttnState[0] = 1;
                } else {
                    tBttnState[0] = 0;
                }
            }
        });
        return tBttnState[0];
    }

    // Method executes sql query to add to database.
    public String exeSQLQuery(String task, String date, String time, int dayState, ConnectionClass connection) {
        String msg = "";
        // If the user leaves out an text field prompt them to fill all fields.
        if (taskText.equals("") || dateText.equals("") || timeText.equals("")) {
            msg = "Please fill out all fields.";
            Log.d("Warning", msg);
        }
        // Otherwise try to execute the sql query.
        else {
            Log.d("Context", "Inside else");
            try {
                Connection con = connection.CONN();
                Log.d("Checking", "If connection is made");
                if (con == null) {
                    Log.d("Warning", "Connection not established");
                    msg = "Please check your internet connection...";
                } else {
                    // Send query to add to database.
                    String addQuery = formatQuery(task, date, time, dayState);
                    Log.d("String", addQuery);

                    Statement st = con.createStatement();
                    st.executeUpdate(addQuery);
                    msg = "Task Added";
                }

            } catch (Exception e) {
                Log.d("Error", "Inside try failed");
                msg = "Sorry, something went wrong.";
                e.printStackTrace();
            }
        }
        return msg;
    }

}